package com.railway.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RefundRequest {

    private Long reservationId;
    private String pnr;
    private Long paymentTransactionId;

    @NotNull(message = "Original amount is required")
    private Double amount;

    private String reason = "Customer requested cancellation";
    private LocalDate journeyDate;
    private String departureTime;
    private String classType = "SL";

    public RefundRequest() {}

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Long getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(Long paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }
}
