package com.railway.auth.service;

import com.railway.auth.dto.*;

public interface AuthService {
    ApiResponse<AuthResponse> register(RegisterRequest request);
    ApiResponse<AuthResponse> login(LoginRequest request);
    ApiResponse<Void> logout(String refreshToken);
    ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest request);
    ApiResponse<String> forgotPassword(ForgotPasswordRequest request);
    ApiResponse<String> verifyOtp(VerifyOtpRequest request);
    ApiResponse<String> resetPassword(ResetPasswordRequest request);
    ApiResponse<String> changePassword(String email, ChangePasswordRequest request);
    void updateUserStatus(Long userId, boolean active);
    void updateUserStatusByEmail(String email, boolean active);
    UserDto getUserById(Long userId);
    java.util.List<UserDto> getAllUsers();
    void updateUserProfile(Long userId, String email, String fullName, String mobile);
}