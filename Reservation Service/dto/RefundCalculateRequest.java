package com.railway.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RefundCalculateRequest {

    @NotNull(message = "Ticket amount is required")
    private Double ticketAmount;

    private LocalDate journeyDate;
    private String departureTime; // "06:10"
    private String classType = "SL";

    public RefundCalculateRequest() {}

    public RefundCalculateRequest(Double ticketAmount, LocalDate journeyDate, String departureTime, String classType) {
        this.ticketAmount = ticketAmount;
        this.journeyDate = journeyDate;
        this.departureTime = departureTime;
        this.classType = classType;
    }

    public Double getTicketAmount() { return ticketAmount; }
    public void setTicketAmount(Double ticketAmount) { this.ticketAmount = ticketAmount; }

    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }
}
