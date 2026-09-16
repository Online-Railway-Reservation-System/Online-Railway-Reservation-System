package com.railway.stationroute.dto;

public class RouteStationResponse {
    private Long id;
    private Long trainId;
    private String stationCode;
    private String stationName;
    private Integer stopSequence;
    private Double distanceFromOriginKm;
    private boolean sourceStation;
    private boolean destinationStation;

    public RouteStationResponse() {}

    public RouteStationResponse(Long id, Long trainId, String stationCode, String stationName, Integer stopSequence, Double distanceFromOriginKm, boolean sourceStation, boolean destinationStation) {
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
}