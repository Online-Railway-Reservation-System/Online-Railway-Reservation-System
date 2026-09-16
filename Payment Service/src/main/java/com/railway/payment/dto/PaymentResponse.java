package com.railway.payment.dto;

import java.time.LocalDateTime;

public class PaymentResponse {
    private Long id;
    private String transactionReference;
    private Long reservationId;
    private String pnr;
    private Long customerId;
    private Double amount;
    private String paymentMethod;
    private String status;
    private String gatewayReference;
    private LocalDateTime timestamp;

    public PaymentResponse() {}

    public PaymentResponse(Long id, String transactionReference, Long reservationId, String pnr, Long customerId, Double amount, String paymentMethod, String status, String gatewayReference, LocalDateTime timestamp) {
        this.id = id;
        this.transactionReference = transactionReference;
        this.reservationId = reservationId;
        this.pnr = pnr;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.gatewayReference = gatewayReference;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGatewayReference() { return gatewayReference; }
    public void setGatewayReference(String gatewayReference) { this.gatewayReference = gatewayReference; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
