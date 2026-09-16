package com.railway.schedulefare.dto;

import java.time.LocalDate;

public class ScheduleResponse {
    private Long id;
    private Long trainId;
    private String departureStationCode;
    private String arrivalStationCode;
    private String departureTime;
    private String arrivalTime;
    private String runningDays;
    private Double durationHours;
    private LocalDate validFrom;
    private LocalDate validUpto;
    private boolean activeStatus;

    public ScheduleResponse() {}

    public ScheduleResponse(Long id, Long trainId, String departureStationCode, String arrivalStationCode, String departureTime, String arrivalTime, String runningDays, Double durationHours, LocalDate validFrom, LocalDate validUpto, boolean activeStatus) {
        this.id = id;
        this.trainId = trainId;
        this.departureStationCode = departureStationCode;
        this.arrivalStationCode = arrivalStationCode;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.runningDays = runningDays;
        this.durationHours = durationHours;
        this.validFrom = validFrom;
        this.validUpto = validUpto;
        this.activeStatus = activeStatus;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public String getDepartureStationCode() { return departureStationCode; }
    public void setDepartureStationCode(String departureStationCode) { this.departureStationCode = departureStationCode; }

    public String getArrivalStationCode() { return arrivalStationCode; }
    public void setArrivalStationCode(String arrivalStationCode) { this.arrivalStationCode = arrivalStationCode; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getRunningDays() { return runningDays; }
    public void setRunningDays(String runningDays) { this.runningDays = runningDays; }

    public Double getDurationHours() { return durationHours; }
    public void setDurationHours(Double durationHours) { this.durationHours = durationHours; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidUpto() { return validUpto; }
    public void setValidUpto(LocalDate validUpto) { this.validUpto = validUpto; }

    public boolean isActiveStatus() { return activeStatus; }
    public void setActiveStatus(boolean activeStatus) { this.activeStatus = activeStatus; }
}