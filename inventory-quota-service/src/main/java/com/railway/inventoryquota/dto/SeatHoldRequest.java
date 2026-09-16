package com.railway.inventoryquota.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class SeatHoldRequest {

    @NotNull(message = "Train ID is required")
    private Long trainId;

    @NotNull(message = "Journey date is required")
    private LocalDate journeyDate;

    @NotBlank(message = "Class type is required")
    private String classType;

    private String quota = "GENERAL";

    @NotNull(message = "Seat count is required")
    @Min(value = 1, message = "Must hold at least 1 seat")
    private Integer seatCount = 1;

    private String holdReference;
    private int holdDurationMinutes = 10;

    private String fromStationCode;
    private String toStationCode;
    private Integer fromStopSeq = 1;
    private Integer toStopSeq = 999;

    public SeatHoldRequest() {}

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public String getQuota() { return quota; }
    public void setQuota(String quota) { this.quota = quota; }

    public Integer getSeatCount() { return seatCount; }
    public void setSeatCount(Integer seatCount) { this.seatCount = seatCount; }

    public String getHoldReference() { return holdReference; }
    public void setHoldReference(String holdReference) { this.holdReference = holdReference; }

    public int getHoldDurationMinutes() { return holdDurationMinutes; }
    public void setHoldDurationMinutes(int holdDurationMinutes) { this.holdDurationMinutes = holdDurationMinutes; }

    public String getFromStationCode() { return fromStationCode; }
    public void setFromStationCode(String fromStationCode) { this.fromStationCode = fromStationCode; }

    public String getToStationCode() { return toStationCode; }
    public void setToStationCode(String toStationCode) { this.toStationCode = toStationCode; }

    public Integer getFromStopSeq() { return fromStopSeq; }
    public void setFromStopSeq(Integer fromStopSeq) { this.fromStopSeq = fromStopSeq; }

    public Integer getToStopSeq() { return toStopSeq; }
    public void setToStopSeq(Integer toStopSeq) { this.toStopSeq = toStopSeq; }
}