package com.railway.customer.service;

import com.railway.customer.dto.ConcessionVerificationRequest;
import com.railway.customer.dto.ConcessionVerificationResponse;
import com.railway.customer.entity.ConcessionVerification;
import com.railway.customer.repository.ConcessionVerificationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class MockConcessionVerificationProvider implements ConcessionVerificationProvider {

    private final ConcessionVerificationRepository verificationRepository;

    public MockConcessionVerificationProvider(ConcessionVerificationRepository verificationRepository) {
        this.verificationRepository = verificationRepository;
    }

    @Override
    public ConcessionVerificationResponse verifyConcession(ConcessionVerificationRequest request) {
        String num = request.getConcessionNumber() != null ? request.getConcessionNumber().trim() : "";
        String type = request.getConcessionType() != null ? request.getConcessionType().trim().toUpperCase() : "";
        String normType = normalizeType(type);

        Optional<ConcessionVerification> recordOpt = verificationRepository
                .findByConcessionNumberAndConcessionType(num, normType);
        if (recordOpt.isEmpty() && !normType.equals(type)) {
            recordOpt = verificationRepository.findByConcessionNumberAndConcessionType(num, type);
        }

        if (recordOpt.isEmpty()) {
            return new ConcessionVerificationResponse(
                    false,
                    type,
                    num,
                    "REJECTED",
                    "Concession record not found in verification registry"
            );
        }

        ConcessionVerification record = recordOpt.get();

        if (!"ACTIVE".equalsIgnoreCase(record.getStatus())) {
            return new ConcessionVerificationResponse(
                    false,
                    type,
                    num,
                    "SUSPENDED",
                    "Concession card status is " + record.getStatus()
            );
        }

        LocalDate today = LocalDate.now();
        if (record.getValidFrom().isAfter(today)) {
            return new ConcessionVerificationResponse(
                    false,
                    type,
                    num,
                    "NOT_YET_VALID",
                    "Concession card is not active yet (valid from " + record.getValidFrom() + ")"
            );
        }

        if (record.getValidUpto().isBefore(today)) {
            return new ConcessionVerificationResponse(
                    false,
                    type,
                    num,
                    "EXPIRED",
                    "Concession card expired on " + record.getValidUpto()
            );
        }

        return new ConcessionVerificationResponse(
                true,
                type,
                num,
                "VERIFIED",
                "Concession verified successfully for holder: " + record.getHolderName()
        );
    }

    private String normalizeType(String type) {
        if (type == null) return "";
        String t = type.trim().toUpperCase();
        if ("DISABILITY".equals(t)) return "DISABLED";
        if ("GOVERNMENT_STAFF".equals(t)) return "DEFENCE";
        return t;
    }
}