package com.railway.auth.repository;

import com.railway.auth.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    Optional<PasswordResetOtp> findTopByEmailOrderByExpiryTimeDesc(String email);
    Optional<PasswordResetOtp> findByEmailAndOtp(String email, String otp);
}