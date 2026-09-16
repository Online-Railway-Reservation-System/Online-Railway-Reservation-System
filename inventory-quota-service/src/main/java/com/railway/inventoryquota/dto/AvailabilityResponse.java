package com.railway.inventoryquota.dto;

import java.time.LocalDate;

public class AvailabilityResponse {
    private Long trainId;
    private LocalDate journeyDate;
    private String classType;
    private String quota;
    private long availableSeats;
    private String status; // AVAILABLE, WL, RAC
    private String fromStationCode;
    private String toStationCode;
    private Integer fromStopSeq;
    private Integer toStopSeq;

    public AvailabilityResponse() {}

    public AvailabilityResponse(Long trainId, LocalDate journeyDate, String classType, String quota, long availableSeats, String status) {
        this.trainId = trainId;
        this.journeyDate = journeyDate;
        this.classType = classType;
        this.quota = quota;
        this.availableSeats = availableSeats;
        this.status = status;
    }

    public AvailabilityResponse(Long trainId, LocalDate journeyDate, String classType, String quota, long availableSeats, String status,
                                String fromStationCode, String toStationCode, Integer fromStopSeq, Integer toStopSeq) {
        this.trainId = trainId;
        this.journeyDate = journeyDate;
        this.classType = classType;
        this.quota = quota;
        this.availableSeats = availableSeats;
        this.status = status;
        this.fromStationCode = fromStationCode;
        this.toStationCode = toStationCode;
        this.fromStopSeq = fromStopSeq;
        this.toStopSeq = toStopSeq;
    }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public String getQuota() { return quota; }
    public void setQuota(String quota) { this.quota = quota; }

    public long getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(long availableSeats) { this.availableSeats = availableSeats; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFromStationCode() { return fromStationCode; }
    public void setFromStationCode(String fromStationCode) { this.fromStationCode = fromStationCode; }

    public String getToStationCode() { return toStationCode; }
    public void setToStationCode(String toStationCode) { this.toStationCode = toStationCode; }

    public Integer getFromStopSeq() { return fromStopSeq; }
    public void setFromStopSeq(Integer fromStopSeq) { this.fromStopSeq = fromStopSeq; }

    public Integer getToStopSeq() { return toStopSeq; }
    public void setToStopSeq(Integer toStopSeq) { this.toStopSeq = toStopSeq; }
}