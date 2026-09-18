package com.railway.payment.event;

import java.time.LocalDateTime;

public class RefundEvent {
    private String eventType; // REFUND_PROCESSED, REFUND_FAILED
    private Long refundId;
    private String refundReference;
    private Long reservationId;
    private String pnr;
    private Double originalAmount;
    private Double refundAmount;
    private Double deductionAmount;
    private String status;
    private LocalDateTime timestamp;

    public RefundEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public RefundEvent(String eventType, Long refundId, String refundReference, Long reservationId, String pnr, Double originalAmount, Double refundAmount, Double deductionAmount, String status) {
        this.eventType = eventType;
        this.refundId = refundId;
        this.refundReference = refundReference;
        this.reservationId = reservationId;
        this.pnr = pnr;
        this.originalAmount = originalAmount;
        this.refundAmount = refundAmount;
        this.deductionAmount = deductionAmount;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getRefundId() { return refundId; }
    public void setRefundId(Long refundId) { this.refundId = refundId; }

    public String getRefundReference() { return refundReference; }
    public void setRefundReference(String refundReference) { this.refundReference = refundReference; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Double getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(Double originalAmount) { this.originalAmount = originalAmount; }

    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }

    public Double getDeductionAmount() { return deductionAmount; }
    public void setDeductionAmount(Double deductionAmount) { this.deductionAmount = deductionAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
