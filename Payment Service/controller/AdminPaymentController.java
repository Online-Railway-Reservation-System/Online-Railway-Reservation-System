package com.railway.payment.controller;

import com.railway.payment.dto.ApiResponse;
import com.railway.payment.dto.PaymentResponse;
import com.railway.payment.dto.RefundResponse;
import com.railway.payment.service.PaymentService;
import com.railway.payment.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/payments")
@Tag(name = "Admin Payments & Refunds", description = "System-wide payment audits and reconciliation")
public class AdminPaymentController {

    private final PaymentService paymentService;
    private final RefundService refundService;

    public AdminPaymentController(PaymentService paymentService, RefundService refundService) {
        this.paymentService = paymentService;
        this.refundService = refundService;
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get all payment transactions paged (Admin)")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getAllPayments(pageable)));
    }

    @GetMapping("/refunds")
    @Operation(summary = "Get all refund transactions paged (Admin)")
    public ResponseEntity<ApiResponse<Page<RefundResponse>>> getAllRefunds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "processedAt"));
        return ResponseEntity.ok(ApiResponse.ok(refundService.getAllRefunds(pageable)));
    }
}
