package com.invoiceng.controller;

import com.invoiceng.dto.request.RequestOtpRequest;
import com.invoiceng.dto.request.VerifyOtpRequest;
import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.dto.response.AuthResponse;
import com.invoiceng.dto.response.OtpResponse;
import com.invoiceng.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Phone OTP authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/request-otp")
    @Operation(summary = "Request OTP", description = "Send OTP to phone number for authentication")
    public ResponseEntity<ApiResponse<OtpResponse>> requestOtp(
            @Valid @RequestBody RequestOtpRequest request
    ) {
        OtpResponse response = authService.requestOtp(request);
        return ResponseEntity.ok(ApiResponse.success(response, "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verify OTP and get authentication tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        AuthResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Get new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        String refreshToken = authHeader.replace("Bearer ", "");
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate the current session")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // JWT is stateless, so logout is handled client-side by removing the token
        // In a production app, you might want to add the token to a blacklist
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}
