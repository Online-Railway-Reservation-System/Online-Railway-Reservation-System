package com.railway.schedulefare.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "train_schedules")
public class TrainSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_id", nullable = false)
    private Long trainId;

    @Column(name = "departure_station_code", nullable = false, length = 10)
    private String departureStationCode;

    @Column(name = "arrival_station_code", nullable = false, length = 10)
    private String arrivalStationCode;

    @Column(name = "departure_time", nullable = false, length = 10)
    private String departureTime; // "06:15"

    @Column(name = "arrival_time", nullable = false, length = 10)
    private String arrivalTime; // "13:00"

    @Column(name = "running_days", nullable = false, length = 50)
    private String runningDays; // "MON,TUE,WED,THU,FRI,SAT,SUN"

    @Column(name = "duration_hours", nullable = false)
    private Double durationHours;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_upto", nullable = false)
    private LocalDate validUpto;

    @Column(name = "active_status", nullable = false)
    private boolean activeStatus = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public TrainSchedule() {}

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}