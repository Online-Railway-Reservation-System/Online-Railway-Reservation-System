package com.railway.customer.controller;

import com.railway.customer.dto.*;
import com.railway.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Management", description = "Customer profile, booking references, and concession verification")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current customer profile")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getMyProfile(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader) {
        Long userId = parseUserId(userIdHeader);
        return ResponseEntity.ok(ApiResponse.ok(customerService.getProfile(userId, emailHeader)));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current customer profile")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateMyProfile(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader,
            @Valid @RequestBody CustomerProfileRequest request) {
        Long userId = parseUserId(userIdHeader);
        return ResponseEntity.ok(ApiResponse.ok(customerService.updateProfile(userId, emailHeader, request)));
    }

    @GetMapping("/me/concessions")
    @Operation(summary = "Get current customer registered concessions")
    public ResponseEntity<ApiResponse<List<ConcessionResponse>>> getMyConcessions(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader) {
        Long userId = parseUserId(userIdHeader);
        return ResponseEntity.ok(ApiResponse.ok(customerService.getCustomerConcessions(userId, emailHeader)));
    }

    @PostMapping("/me/concessions")
    @Operation(summary = "Add and verify a concession card for current customer")
    public ResponseEntity<ApiResponse<ConcessionResponse>> addConcession(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader,
            @Valid @RequestBody ConcessionRequest request) {
        Long userId = parseUserId(userIdHeader);
        return ResponseEntity.ok(ApiResponse.ok(customerService.addConcession(userId, emailHeader, request)));
    }

    @PostMapping("/concessions/verify")
    @Operation(summary = "Automatic concession verification endpoint (used by Reservation Service)")
    public ResponseEntity<ApiResponse<ConcessionVerificationResponse>> verifyConcession(
            @Valid @RequestBody ConcessionVerificationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.verifyConcession(request)));
    }

    @GetMapping("/me/bookings")
    @Operation(summary = "Get customer bookings overview")
    public ResponseEntity<ApiResponse<List<String>>> getMyBookings() {
        return ResponseEntity.ok(ApiResponse.ok("Booking history can be fetched via Reservation Service", Collections.emptyList()));
    }

    @GetMapping("/me/bookings/upcoming")
    public ResponseEntity<ApiResponse<List<String>>> getUpcomingBookings() {
        return ResponseEntity.ok(ApiResponse.ok("Upcoming bookings", Collections.emptyList()));
    }

    @GetMapping("/me/bookings/completed")
    public ResponseEntity<ApiResponse<List<String>>> getCompletedBookings() {
        return ResponseEntity.ok(ApiResponse.ok("Completed bookings", Collections.emptyList()));
    }

    @GetMapping("/me/bookings/cancelled")
    public ResponseEntity<ApiResponse<List<String>>> getCancelledBookings() {
        return ResponseEntity.ok(ApiResponse.ok("Cancelled bookings", Collections.emptyList()));
    }

    private Long parseUserId(String userIdStr) {
        if (userIdStr != null && !userIdStr.isBlank()) {
            try {
                return Long.parseLong(userIdStr.trim());
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}