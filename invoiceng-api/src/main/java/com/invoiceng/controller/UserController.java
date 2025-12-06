package com.invoiceng.controller;

import com.invoiceng.dto.request.UpdateUserRequest;
import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.dto.response.UserResponse;
import com.invoiceng.security.CurrentUser;
import com.invoiceng.security.UserPrincipal;
import com.invoiceng.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get the currently authenticated user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @CurrentUser UserPrincipal currentUser
    ) {
        UserResponse response = userService.getCurrentUser(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/me")
    @Operation(summary = "Update profile", description = "Update the current user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse response = userService.updateUser(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Profile updated successfully"));
    }
}
