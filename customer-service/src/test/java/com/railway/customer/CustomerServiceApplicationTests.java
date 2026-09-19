package com.railway.customer;

import com.railway.customer.dto.*;
import com.railway.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceApplicationTests {

    @Autowired
    private CustomerService customerService;

    @Test
    void testProfileAndConcessionAutoVerification() {
        CustomerProfileRequest profileReq = new CustomerProfileRequest();
        profileReq.setFullName("Alice Smith");
        profileReq.setMobile("9998887776");
        profileReq.setAddress("221B Baker St");
        profileReq.setGender("FEMALE");

        CustomerProfileResponse profile = customerService.updateProfile(10L, "alice@test.com", profileReq);
        assertNotNull(profile);
        assertEquals("Alice Smith", profile.getFullName());

        // Test Concession Mock Verification with seed record
        customerService.addVerificationRecord(
                new ConcessionVerificationRequest(10L, "DISABILITY", "DIV999000"),
                "Alice Smith"
        );

        ConcessionVerificationRequest verReq = new ConcessionVerificationRequest(10L, "DISABILITY", "DIV999000");
        ConcessionVerificationResponse verResp = customerService.verifyConcession(verReq);

        assertNotNull(verResp);
        assertTrue(verResp.isEligible());
        assertEquals("VERIFIED", verResp.getVerificationStatus());

        // Test Invalid Concession
        ConcessionVerificationRequest invalidReq = new ConcessionVerificationRequest(10L, "DISABILITY", "NONEXISTENT");
        ConcessionVerificationResponse invalidResp = customerService.verifyConcession(invalidReq);

        assertNotNull(invalidResp);
        assertFalse(invalidResp.isEligible());
        assertEquals("REJECTED", invalidResp.getVerificationStatus());
    }
}