package com.railway.auth.controller;

import com.railway.auth.dto.*;
import com.railway.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration, login, JWT token refresh, OTP, and password management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new customer")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        ApiResponse<AuthResponse> response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT access & refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and invalidate refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam(required = false) String refreshToken) {
        return ResponseEntity.ok(authService.logout(refreshToken));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Generate new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset OTP")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP for password reset")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using verified OTP")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password for authenticated user")
    public ResponseEntity<ApiResponse<String>> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                              @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(userDetails.getUsername(), request));
    }

    @RequestMapping(value = "/users/{userId}/status", method = {RequestMethod.PATCH, RequestMethod.POST, RequestMethod.PUT})
    @Operation(summary = "Update user active status by userId (Admin)")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean active) {
        authService.updateUserStatus(userId, active);
        return ResponseEntity.ok(ApiResponse.message("User status updated successfully"));
    }

    @RequestMapping(value = "/users/email/status", method = {RequestMethod.PATCH, RequestMethod.POST, RequestMethod.PUT})
    @Operation(summary = "Update user active status by email (Admin)")
    public ResponseEntity<ApiResponse<Void>> updateUserStatusByEmail(
            @RequestParam String email,
            @RequestParam boolean active) {
        authService.updateUserStatusByEmail(email, active);
        return ResponseEntity.ok(ApiResponse.message("User status updated successfully"));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user details by userId")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(authService.getUserById(userId)));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users (Admin / Internal Sync)")
    public ResponseEntity<ApiResponse<java.util.List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(authService.getAllUsers()));
    }

    @PutMapping("/users/profile")
    @Operation(summary = "Update user basic profile details (Internal / Admin)")
    public ResponseEntity<ApiResponse<Void>> updateUserProfile(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String mobile) {
        authService.updateUserProfile(userId, email, fullName, mobile);
        return ResponseEntity.ok(ApiResponse.message("User profile updated successfully in auth service"));
    }
}