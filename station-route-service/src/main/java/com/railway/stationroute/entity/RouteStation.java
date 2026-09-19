package com.railway.stationroute.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "route_stations")
public class RouteStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_id", nullable = false)
    private Long trainId;

    @Column(name = "station_code", nullable = false, length = 10)
    private String stationCode;

    @Column(name = "station_name", nullable = false, length = 100)
    private String stationName;

    @Column(name = "stop_sequence", nullable = false)
    private Integer stopSequence;

    @Column(name = "distance_from_origin_km", nullable = false)
    private Double distanceFromOriginKm;

    @Column(name = "is_source", nullable = false)
    private boolean sourceStation = false;

    @Column(name = "is_destination", nullable = false)
    private boolean destinationStation = false;

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

    public RouteStation() {}

    public RouteStation(Long id, Long trainId, String stationCode, String stationName, Integer stopSequence, Double distanceFromOriginKm, boolean sourceStation, boolean destinationStation) {
        this.id = id;
        this.trainId = trainId;
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.stopSequence = stopSequence;
        this.distanceFromOriginKm = distanceFromOriginKm;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public String getStationCode() { return stationCode; }
    public void setStationCode(String stationCode) { this.stationCode = stationCode; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public Integer getStopSequence() { return stopSequence; }
    public void setStopSequence(Integer stopSequence) { this.stopSequence = stopSequence; }

    public Double getDistanceFromOriginKm() { return distanceFromOriginKm; }
    public void setDistanceFromOriginKm(Double distanceFromOriginKm) { this.distanceFromOriginKm = distanceFromOriginKm; }

    public boolean isSourceStation() { return sourceStation; }
    public void setSourceStation(boolean sourceStation) { this.sourceStation = sourceStation; }

    public boolean isDestinationStation() { return destinationStation; }
    public void setDestinationStation(boolean destinationStation) { this.destinationStation = destinationStation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}