package com.railway.payment.controller;

import com.railway.payment.dto.ApiResponse;
import com.railway.payment.dto.PaymentRequest;
import com.railway.payment.dto.PaymentResponse;
import com.railway.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment transaction processing, gateway verification, and transaction status")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    @Operation(summary = "Process ticket reservation payment with idempotency protection")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody PaymentRequest request) {

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            request.setIdempotencyKey(idempotencyKey);
        }
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            try {
                request.setCustomerId(Long.parseLong(userIdHeader));
            } catch (NumberFormatException ignored) {}
        }

        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Payment processed successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment transaction by ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentById(id)));
    }

    @GetMapping("/ref/{transactionReference}")
    @Operation(summary = "Get payment transaction by unique transaction reference")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByReference(@PathVariable String transactionReference) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentByTransactionReference(transactionReference)));
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Get payment transaction by reservation ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentByReservationId(reservationId)));
    }

    @GetMapping("/pnr/{pnr}")
    @Operation(summary = "Get payment transaction by PNR")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByPnr(@PathVariable String pnr) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentByPnr(pnr)));
    }

    @GetMapping("/my")
    @Operation(summary = "Get current customer payment transaction history")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyPayments(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        Long customerId = parseUserId(userIdHeader);
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentsByCustomerId(customerId)));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get payment history for customer ID (Support / Admin)")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getCustomerPayments(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentsByCustomerId(customerId)));
    }

    private Long parseUserId(String userIdStr) {
        if (userIdStr != null && !userIdStr.isBlank()) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException ignored) {}
        }
        return 1L;
    }
}
