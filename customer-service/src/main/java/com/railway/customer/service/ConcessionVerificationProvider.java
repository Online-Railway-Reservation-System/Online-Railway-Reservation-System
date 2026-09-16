package com.railway.customer.service;

import com.railway.customer.dto.ConcessionVerificationRequest;
import com.railway.customer.dto.ConcessionVerificationResponse;

public interface ConcessionVerificationProvider {
	ConcessionVerificationResponse verifyConcession(ConcessionVerificationRequest request);
}