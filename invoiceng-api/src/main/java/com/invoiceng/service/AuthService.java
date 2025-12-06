package com.invoiceng.service;

import com.invoiceng.dto.request.RequestOtpRequest;
import com.invoiceng.dto.request.VerifyOtpRequest;
import com.invoiceng.dto.response.AuthResponse;
import com.invoiceng.dto.response.OtpResponse;
import com.invoiceng.dto.response.UserResponse;
import com.invoiceng.entity.OtpRequest;
import com.invoiceng.entity.User;
import com.invoiceng.exception.RateLimitException;
import com.invoiceng.exception.UnauthorizedException;
import com.invoiceng.exception.ValidationException;
import com.invoiceng.repository.OtpRequestRepository;
import com.invoiceng.repository.UserRepository;
import com.invoiceng.security.JwtTokenProvider;
import com.invoiceng.util.PhoneNumberFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRequestRepository otpRequestRepository;
    private final SmsService smsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final PhoneNumberFormatter phoneFormatter;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private static final String DEV_OTP = "123456";
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_OTP_REQUESTS_PER_15_MIN = 3;
    private static final int MAX_OTP_ATTEMPTS = 3;

    @Transactional
    public OtpResponse requestOtp(RequestOtpRequest request) {
        String phone = phoneFormatter.formatToInternational(request.getPhone());
        log.info("OTP request for phone: {}", maskPhone(phone));

        // Rate limiting
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        long recentRequests = otpRequestRepository.countRecentRequests(phone, fifteenMinutesAgo);

        if (recentRequests >= MAX_OTP_REQUESTS_PER_15_MIN) {
            int waitSeconds = 15 * 60; // 15 minutes
            throw new RateLimitException(
                    "Too many OTP requests. Please try again in 15 minutes.",
                    waitSeconds
            );
        }

        String pinId;
        String otp;

        // In dev mode, use fixed OTP and skip SMS
        if (isDevMode()) {
            log.warn("DEV MODE: Using fixed OTP '{}' - SMS skipped", DEV_OTP);
            pinId = "dev-" + System.currentTimeMillis();
            otp = DEV_OTP;
        } else {
            // Send OTP via Termii in production
            pinId = smsService.sendOtp(phone);
            otp = generateOtp();
        }

        String otpHash = passwordEncoder.encode(otp);

        // Save OTP request
        OtpRequest otpRequest = OtpRequest.builder()
                .phone(phone)
                .otpHash(otpHash)
                .pinId(pinId)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();

        otpRequestRepository.save(otpRequest);

        log.info("OTP request saved with pinId: {}", pinId);

        return OtpResponse.builder()
                .message("OTP sent successfully")
                .expiresIn(OTP_EXPIRY_MINUTES * 60)
                .build();
    }

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String phone = phoneFormatter.formatToInternational(request.getPhone());
        log.info("Verifying OTP for phone: {}", maskPhone(phone));

        // Find latest valid OTP request
        OtpRequest otpRequest = otpRequestRepository
                .findLatestValidOtp(phone, LocalDateTime.now())
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired OTP"));

        // Check attempts
        if (otpRequest.hasExceededAttempts()) {
            throw new UnauthorizedException("Maximum OTP attempts exceeded. Please request a new OTP.");
        }

        boolean isValid;

        // In dev mode, verify against fixed OTP
        if (isDevMode()) {
            isValid = DEV_OTP.equals(request.getOtp());
            log.warn("DEV MODE: Verifying OTP '{}' against fixed OTP - result: {}", request.getOtp(), isValid);
        } else {
            // Verify with Termii in production
            isValid = smsService.verifyOtp(otpRequest.getPinId(), request.getOtp());
        }

        if (!isValid) {
            otpRequest.incrementAttempts();
            otpRequestRepository.save(otpRequest);
            throw new UnauthorizedException("Invalid OTP");
        }

        // Mark as verified
        otpRequestRepository.markAsVerified(otpRequest.getId());

        // Get or create user
        boolean isNewUser = !userRepository.existsByPhone(phone);
        User user = userRepository.findByPhone(phone)
                .orElseGet(() -> createNewUser(phone));

        // Generate tokens
        String token = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        log.info("User authenticated: {}, isNew: {}", user.getId(), isNewUser);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getExpirationInSeconds())
                .user(UserResponse.fromEntity(user))
                .isNewUser(isNewUser)
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Invalid token type");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        String newToken = jwtTokenProvider.generateToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtTokenProvider.getExpirationInSeconds())
                .user(UserResponse.fromEntity(user))
                .isNewUser(false)
                .build();
    }

    public User getCurrentUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    private User createNewUser(String phone) {
        User user = User.builder()
                .phone(phone)
                .subscriptionTier("free")
                .invoiceCountThisMonth(0)
                .invoiceCountResetAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    private String generateOtp() {
        // Generate 6-digit OTP
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 6) {
            return "****";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private boolean isDevMode() {
        return "dev".equalsIgnoreCase(activeProfile);
    }
}
