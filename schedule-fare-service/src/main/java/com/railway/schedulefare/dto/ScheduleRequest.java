package com.railway.schedulefare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ScheduleRequest {

    @NotNull(message = "Train ID is required")
    private Long trainId;

    @NotBlank(message = "Departure station code is required")
    private String departureStationCode;

    @NotBlank(message = "Arrival station code is required")
    private String arrivalStationCode;

    @NotBlank(message = "Departure time is required (HH:mm)")
    private String departureTime;

    @NotBlank(message = "Arrival time is required (HH:mm)")
    private String arrivalTime;

    @NotBlank(message = "Running days is required")
    private String runningDays;

    @NotNull(message = "Duration in hours is required")
    private Double durationHours;

    private LocalDate validFrom = LocalDate.now();
    private LocalDate validUpto = LocalDate.now().plusYears(1);
    private boolean activeStatus = true;

    public ScheduleRequest() {}

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