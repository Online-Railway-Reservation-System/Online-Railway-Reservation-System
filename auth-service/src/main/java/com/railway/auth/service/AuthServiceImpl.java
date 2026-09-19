package com.railway.auth.service;

import com.railway.auth.dto.*;
import com.railway.auth.entity.PasswordResetOtp;
import com.railway.auth.entity.RefreshToken;
import com.railway.auth.entity.Role;
import com.railway.auth.entity.User;
import com.railway.auth.exception.BadRequestException;
import com.railway.auth.exception.ResourceNotFoundException;
import com.railway.auth.exception.UnauthorizedException;
import com.railway.auth.repository.PasswordResetOtpRepository;
import com.railway.auth.repository.RefreshTokenRepository;
import com.railway.auth.repository.UserRepository;
import com.railway.auth.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final org.springframework.mail.javamail.JavaMailSender mailSender;

    @Value("${app.jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenDurationMs;

    public AuthServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordResetOtpRepository otpRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) org.springframework.mail.javamail.JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailSender = mailSender;
    }

    @Override
    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        Optional<User> existingUserOpt = userRepository.findByEmail(request.getEmail().toLowerCase().trim());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (!existingUser.isActive()) {
                throw new BadRequestException("Registration denied: This email address (" + request.getEmail() + ") is suspended/blocked by the administrator. Login and re-registration are prohibited.");
            }
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setMobile(request.getMobile());
        user.setRole(Role.CUSTOMER);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        String accessToken = jwtTokenProvider.generateToken(savedUser.getEmail(), savedUser.getId(), savedUser.getRole().name());
        RefreshToken refreshToken = createRefreshToken(savedUser.getId());

        AuthResponse authResponse = new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().name()
        );

        return ApiResponse.ok("User registered successfully", authResponse);
    }

    @Override
    public ApiResponse<AuthResponse> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new UnauthorizedException("Your account has been suspended/blocked by the administrator. Please contact customer support.");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        RefreshToken refreshToken = createRefreshToken(user.getId());

        AuthResponse authResponse = new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );

        return ApiResponse.ok("Login successful", authResponse);
    }

    @Override
    public ApiResponse<Void> logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
        return ApiResponse.message("Logged out successfully");
    }

    @Override
    public ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new UnauthorizedException("Refresh token expired. Please login again.");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        AuthResponse response = new AuthResponse(
                newAccessToken,
                token.getToken(),
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );

        return ApiResponse.ok("Token refreshed successfully", response);
    }

    @Override
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("No account registered with this email"));

        String otp = String.format("%06d", new Random().nextInt(999999));
        PasswordResetOtp resetOtp = new PasswordResetOtp(user.getEmail(), otp, LocalDateTime.now().plusMinutes(10));
        otpRepository.save(resetOtp);

        if (mailSender != null) {
            try {
                org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
                message.setFrom("abulhasanrathinamohamed@gmail.com");
                message.setTo(user.getEmail());
                message.setSubject("Password Reset OTP - Railway Reservation");
                message.setText("Dear " + user.getFullName() + ",\n\nYour OTP for password reset is: " + otp + "\n\nThis OTP is valid for 10 minutes.\n\nRegards,\nRailway Reservation Team");
                mailSender.send(message);
            } catch (Exception e) {
                // Log and continue gracefully
                System.err.println("Failed to send OTP email: " + e.getMessage());
            }
        }

        return ApiResponse.ok("OTP generated and sent to email successfully", "OTP: " + otp);
    }

    @Override
    public ApiResponse<String> verifyOtp(VerifyOtpRequest request) {
        PasswordResetOtp otpRecord = otpRepository.findTopByEmailOrderByExpiryTimeDesc(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadRequestException("No OTP found for this email"));

        if (!otpRecord.getOtp().equals(request.getOtp().trim())) {
            throw new BadRequestException("Invalid OTP");
        }

        if (otpRecord.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new OTP.");
        }

        otpRecord.setVerified(true);
        otpRepository.save(otpRecord);

        return ApiResponse.ok("OTP verified successfully. You may now reset your password.", "VERIFIED");
    }

    @Override
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        PasswordResetOtp otpRecord = otpRepository.findByEmailAndOtp(request.getEmail().toLowerCase().trim(), request.getOtp().trim())
                .orElseThrow(() -> new BadRequestException("Invalid email or OTP combination"));

        if (otpRecord.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new OTP.");
        }

        otpRecord.setVerified(true);

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpRepository.delete(otpRecord);
        refreshTokenRepository.deleteByUserId(user.getId());

        return ApiResponse.ok("Password reset successfully. Please login with your new password.", "SUCCESS");
    }

    @Override
    public ApiResponse<String> changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ApiResponse.ok("Password changed successfully", "SUCCESS");
    }

    private RefreshToken createRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        RefreshToken token = new RefreshToken(
                UUID.randomUUID().toString(),
                userId,
                Instant.now().plusMillis(refreshTokenDurationMs)
        );
        return refreshTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setActive(active);
        userRepository.save(user);
        if (!active) {
            try {
                refreshTokenRepository.deleteByUserId(userId);
            } catch (Exception ignored) {}
        }
    }

    @Override
    @Transactional
    public void updateUserStatusByEmail(String email, boolean active) {
        if (email == null || email.isBlank()) return;
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        user.setActive(active);
        userRepository.save(user);
        if (!active) {
            try {
                refreshTokenRepository.deleteByUserId(user.getId());
            } catch (Exception ignored) {}
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getMobile(),
                user.getRole().name(),
                user.isActive()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getEmail(),
                        user.getFullName(),
                        user.getMobile(),
                        user.getRole().name(),
                        user.isActive()
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUserProfile(Long userId, String email, String fullName, String mobile) {
        Optional<User> userOpt = Optional.empty();
        if (userId != null && userId > 0) {
            userOpt = userRepository.findById(userId);
        }
        if (userOpt.isEmpty() && email != null && !email.isBlank()) {
            userOpt = userRepository.findByEmail(email.toLowerCase().trim());
        }
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (fullName != null && !fullName.isBlank()) {
                user.setFullName(fullName.trim());
            }
            if (mobile != null && !mobile.isBlank()) {
                user.setMobile(mobile.trim());
            }
            userRepository.save(user);
        }
    }
}