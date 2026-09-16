package com.railway.search.dto;

public class FareCalculationDto {
    private Long trainId;
    private Double finalPayableFare;

    public FareCalculationDto() {}

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public Double getFinalPayableFare() { return finalPayableFare; }
    public void setFinalPayableFare(Double finalPayableFare) { this.finalPayableFare = finalPayableFare; }
}