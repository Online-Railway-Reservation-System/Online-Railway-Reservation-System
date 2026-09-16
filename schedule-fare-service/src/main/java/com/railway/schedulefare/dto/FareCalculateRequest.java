package com.railway.schedulefare.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FareCalculateRequest {

    @NotNull(message = "Train ID is required")
    private Long trainId;

    @NotBlank(message = "Source station code is required")
    private String sourceStationCode;

    @NotBlank(message = "Destination station code is required")
    private String destinationStationCode;

    @NotBlank(message = "Class type is required (1A, 2A, 3A, SL, CC, 2S)")
    private String classType;

    private String quota = "GENERAL"; // GENERAL, TATKAL, LADIES

    @NotNull(message = "Passenger count is required")
    @Min(value = 1, message = "At least 1 passenger is required")
    private Integer passengerCount = 1;

    private Double distanceKm; // Optional, defaults to standard distance

    private String concessionType; // DISABILITY, GOVERNMENT_STAFF, SENIOR_CITIZEN
    private boolean concessionVerified = false;

    public FareCalculateRequest() {}

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public String getSourceStationCode() { return sourceStationCode; }
    public void setSourceStationCode(String sourceStationCode) { this.sourceStationCode = sourceStationCode; }

    public String getDestinationStationCode() { return destinationStationCode; }
    public void setDestinationStationCode(String destinationStationCode) { this.destinationStationCode = destinationStationCode; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public String getQuota() { return quota; }
    public void setQuota(String quota) { this.quota = quota; }

    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public String getConcessionType() { return concessionType; }
    public void setConcessionType(String concessionType) { this.concessionType = concessionType; }

    public boolean isConcessionVerified() { return concessionVerified; }
    public void setConcessionVerified(boolean concessionVerified) { this.concessionVerified = concessionVerified; }
}