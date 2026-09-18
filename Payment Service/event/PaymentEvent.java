package com.railway.payment.event;

import java.time.LocalDateTime;

public class PaymentEvent {
    private String eventType; // PAYMENT_SUCCESS, PAYMENT_FAILED
    private Long paymentId;
    private String transactionReference;
    private Long reservationId;
    private String pnr;
    private Long customerId;
    private Double amount;
    private String status;
    private LocalDateTime timestamp;

    public PaymentEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public PaymentEvent(String eventType, Long paymentId, String transactionReference, Long reservationId, String pnr, Long customerId, Double amount, String status) {
        this.eventType = eventType;
        this.paymentId = paymentId;
        this.transactionReference = transactionReference;
        this.reservationId = reservationId;
        this.pnr = pnr;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
