package com.railway.payment.dto;

import java.time.LocalDateTime;

public class RefundResponse {
    private Long id;
    private String refundReference;
    private Long paymentTransactionId;
    private Long reservationId;
    private String pnr;
    private Double originalAmount;
    private Double deductionAmount;
    private Double refundAmount;
    private String status;
    private String reason;
    private LocalDateTime processedAt;

    public RefundResponse() {}

    public RefundResponse(Long id, String refundReference, Long paymentTransactionId, Long reservationId, String pnr, Double originalAmount, Double deductionAmount, Double refundAmount, String status, String reason, LocalDateTime processedAt) {
        this.id = id;
        this.refundReference = refundReference;
        this.paymentTransactionId = paymentTransactionId;
        this.reservationId = reservationId;
        this.pnr = pnr;
        this.originalAmount = originalAmount;
        this.deductionAmount = deductionAmount;
        this.refundAmount = refundAmount;
        this.status = status;
        this.reason = reason;
        this.processedAt = processedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRefundReference() { return refundReference; }
    public void setRefundReference(String refundReference) { this.refundReference = refundReference; }

    public Long getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(Long paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Double getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(Double originalAmount) { this.originalAmount = originalAmount; }

    public Double getDeductionAmount() { return deductionAmount; }
    public void setDeductionAmount(Double deductionAmount) { this.deductionAmount = deductionAmount; }

    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
