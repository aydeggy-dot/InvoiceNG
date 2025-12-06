package com.invoiceng.service;

import com.invoiceng.dto.request.UpdateUserRequest;
import com.invoiceng.dto.response.UserResponse;
import com.invoiceng.entity.User;
import com.invoiceng.exception.ResourceNotFoundException;
import com.invoiceng.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getBusinessName() != null) {
            user.setBusinessName(request.getBusinessName());
        }
        if (request.getBusinessAddress() != null) {
            user.setBusinessAddress(request.getBusinessAddress());
        }
        if (request.getBankName() != null) {
            user.setBankName(request.getBankName());
        }
        if (request.getBankCode() != null) {
            user.setBankCode(request.getBankCode());
        }
        if (request.getAccountNumber() != null) {
            user.setAccountNumber(request.getAccountNumber());
        }
        if (request.getAccountName() != null) {
            user.setAccountName(request.getAccountName());
        }

        user = userRepository.save(user);
        log.info("Updated user profile: {}", userId);

        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateLogo(UUID userId, String logoUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setLogoUrl(logoUrl);
        user = userRepository.save(user);

        return UserResponse.fromEntity(user);
    }
}
