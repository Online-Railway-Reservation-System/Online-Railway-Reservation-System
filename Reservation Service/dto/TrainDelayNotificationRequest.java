package com.railway.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class TrainDelayNotificationRequest {

    @NotBlank(message = "Train number is required")
    private String trainNumber;

    @NotNull(message = "Journey date is required")
    private LocalDate journeyDate;

    @NotBlank(message = "Delay duration is required")
    private String delayTime; // e.g. "45 mins", "1 hour 30 mins"

    private String reason; // e.g. "Track maintenance near Rohri", "Signal issue"

    public TrainDelayNotificationRequest() {}

    public TrainDelayNotificationRequest(String trainNumber, LocalDate journeyDate, String delayTime, String reason) {
        this.trainNumber = trainNumber;
        this.journeyDate = journeyDate;
        this.delayTime = delayTime;
        this.reason = reason;
    }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }

    public String getDelayTime() { return delayTime; }
    public void setDelayTime(String delayTime) { this.delayTime = delayTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
