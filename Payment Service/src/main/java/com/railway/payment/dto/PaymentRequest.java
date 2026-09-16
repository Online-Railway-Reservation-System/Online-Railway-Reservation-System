package com.railway.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    private Long customerId;
    private Long reservationId;
    private String pnr;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "1.0", message = "Payment amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Payment method is required (CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING)")
    private String paymentMethod = "CREDIT_CARD";

    private String idempotencyKey;

    public PaymentRequest() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
