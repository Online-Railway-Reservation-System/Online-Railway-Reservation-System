package com.railway.payment.controller;

import com.railway.payment.dto.ApiResponse;
import com.railway.payment.dto.RefundCalculateRequest;
import com.railway.payment.dto.RefundCalculateResponse;
import com.railway.payment.dto.RefundRequest;
import com.railway.payment.dto.RefundResponse;
import com.railway.payment.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/refunds")
@Tag(name = "Refunds", description = "Cancellation charge evaluation, refund processing, and settlement records")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate cancellation charges and net refund amount based on IRCTC policy")
    public ResponseEntity<ApiResponse<RefundCalculateResponse>> calculateRefund(
            @Valid @RequestBody RefundCalculateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(refundService.calculateRefund(request)));
    }

    @PostMapping("/process")
    @Operation(summary = "Process ticket cancellation refund transaction")
    public ResponseEntity<ApiResponse<RefundResponse>> processRefund(
            @Valid @RequestBody RefundRequest request) {
        RefundResponse response = refundService.processRefund(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Refund processed successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get refund transaction by ID")
    public ResponseEntity<ApiResponse<RefundResponse>> getRefundById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(refundService.getRefundById(id)));
    }

    @GetMapping("/ref/{refundReference}")
    @Operation(summary = "Get refund transaction by refund reference")
    public ResponseEntity<ApiResponse<RefundResponse>> getRefundByReference(@PathVariable String refundReference) {
        return ResponseEntity.ok(ApiResponse.ok(refundService.getRefundByReference(refundReference)));
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Get refund transaction by reservation ID")
    public ResponseEntity<ApiResponse<RefundResponse>> getRefundByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.ok(refundService.getRefundByReservationId(reservationId)));
    }

    @GetMapping("/pnr/{pnr}")
    @Operation(summary = "Get refund transaction by PNR")
    public ResponseEntity<ApiResponse<RefundResponse>> getRefundByPnr(@PathVariable String pnr) {
        return ResponseEntity.ok(ApiResponse.ok(refundService.getRefundByPnr(pnr)));
    }
}
