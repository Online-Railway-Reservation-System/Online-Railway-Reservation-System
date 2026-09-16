package com.railway.train.dto;

import jakarta.validation.constraints.NotBlank;

public class TrainRequest {

    @NotBlank(message = "Train number is required")
    private String trainNumber;

    @NotBlank(message = "Train name is required")
    private String trainName;

    @NotBlank(message = "Train type is required (e.g., EXPRESS, SUPERFAST)")
    private String trainType;

    private boolean activeStatus = true;

    public TrainRequest() {}

    public TrainRequest(String trainNumber, String trainName, String trainType, boolean activeStatus) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.trainType = trainType;
        this.activeStatus = activeStatus;
    }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }

    public String getTrainType() { return trainType; }
    public void setTrainType(String trainType) { this.trainType = trainType; }

    public boolean isActiveStatus() { return activeStatus; }
    public void setActiveStatus(boolean activeStatus) { this.activeStatus = activeStatus; }
}