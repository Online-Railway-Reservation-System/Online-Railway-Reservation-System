package com.railway.customer.service;

import com.railway.customer.dto.*;
import java.util.List;

public interface CustomerService {
    CustomerProfileResponse getProfile(Long userId, String email);
    CustomerProfileResponse updateProfile(Long userId, String email, CustomerProfileRequest request);
    List<ConcessionResponse> getCustomerConcessions(Long userId, String email);
    ConcessionResponse addConcession(Long userId, String email, ConcessionRequest request);
    ConcessionVerificationResponse verifyConcession(ConcessionVerificationRequest request);

    // Admin operations
    List<CustomerProfileResponse> getAllCustomers();
    CustomerProfileResponse getCustomerById(Long customerId);
    CustomerProfileResponse updateCustomerStatus(Long customerId, String status);
    void addVerificationRecord(ConcessionVerificationRequest request, String holderName);
}