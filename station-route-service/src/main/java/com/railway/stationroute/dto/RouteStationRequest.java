package com.railway.stationroute.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RouteStationRequest {

    @NotNull(message = "Train ID is required")
    private Long trainId;

    @NotBlank(message = "Station code is required")
    private String stationCode;

    @NotBlank(message = "Station name is required")
    private String stationName;

    @NotNull(message = "Stop sequence is required")
    @Min(value = 1, message = "Stop sequence must be at least 1")
    private Integer stopSequence;

    @NotNull(message = "Distance from origin is required")
    @Min(value = 0, message = "Distance must be non-negative")
    private Double distanceFromOriginKm;

    private boolean sourceStation = false;
    private boolean destinationStation = false;

    public RouteStationRequest() {}

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
}