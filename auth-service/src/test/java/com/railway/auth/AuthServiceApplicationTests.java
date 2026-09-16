package com.railway.auth;

import com.railway.auth.dto.AuthResponse;
import com.railway.auth.dto.LoginRequest;
import com.railway.auth.dto.RegisterRequest;
import com.railway.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceApplicationTests {

    @Autowired
    private AuthService authService;

    @Test
    void testRegisterAndLoginFlow() {
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setEmail("testuser@example.com");
        registerReq.setPassword("Secret@123");
        registerReq.setFullName("John Doe");
        registerReq.setMobile("9876500000");

        var regResponse = authService.register(registerReq);
        assertNotNull(regResponse);
        assertTrue(regResponse.isSuccess());
        assertNotNull(regResponse.getData().getAccessToken());

        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("testuser@example.com");
        loginReq.setPassword("Secret@123");

        var loginResponse = authService.login(loginReq);
        assertNotNull(loginResponse);
        assertTrue(loginResponse.isSuccess());
        assertEquals("testuser@example.com", loginResponse.getData().getEmail());
        assertEquals("CUSTOMER", loginResponse.getData().getRole());
    }
}