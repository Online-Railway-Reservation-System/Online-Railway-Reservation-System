package com.railway.customer.controller;

import com.railway.customer.dto.ApiResponse;
import com.railway.customer.dto.CustomerProfileResponse;
import com.railway.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/customers")
@Tag(name = "Admin Customer Management", description = "Administrator views and updates customer statuses")
public class AdminCustomerController {

    private final CustomerService customerService;

    public AdminCustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "Get all customer profiles")
    public ResponseEntity<ApiResponse<List<CustomerProfileResponse>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getAllCustomers()));
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by id")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getCustomerById(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getCustomerById(customerId)));
    }

    @RequestMapping(value = "/{customerId}/status", method = {RequestMethod.PATCH, RequestMethod.POST, RequestMethod.PUT})
    @Operation(summary = "Update customer account status (ACTIVE, BLOCKED, SUSPENDED)")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateStatus(
            @PathVariable Long customerId,
            @RequestParam(required = false) String status,
            @RequestBody(required = false) Map<String, String> body) {
        String finalStatus = status;
        if (finalStatus == null || finalStatus.isBlank()) {
            if (body != null && body.containsKey("status")) {
                finalStatus = body.get("status");
            }
        }
        if (finalStatus == null || finalStatus.isBlank()) {
            finalStatus = "ACTIVE";
        }
        return ResponseEntity.ok(ApiResponse.ok(customerService.updateCustomerStatus(customerId, finalStatus)));
    }
}