package com.railway.payment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refund_transactions", indexes = {
        @Index(name = "idx_ref_pnr", columnList = "pnr"),
        @Index(name = "idx_ref_res", columnList = "reservation_id")
})
public class RefundTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refund_reference", nullable = false, unique = true, length = 64)
    private String refundReference;

    @Column(name = "payment_transaction_id")
    private Long paymentTransactionId;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(length = 20)
    private String pnr;

    @Column(name = "original_amount", nullable = false)
    private Double originalAmount;

    @Column(name = "deduction_amount", nullable = false)
    private Double deductionAmount;

    @Column(name = "refund_amount", nullable = false)
    private Double refundAmount;

    @Column(length = 255)
    private String reason;

    @Column(nullable = false, length = 20)
    private String status = "PROCESSED"; // PROCESSED, FAILED

    @Column(name = "processed_at", nullable = false, updatable = false)
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        this.processedAt = LocalDateTime.now();
    }

    public RefundTransaction() {}

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

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
