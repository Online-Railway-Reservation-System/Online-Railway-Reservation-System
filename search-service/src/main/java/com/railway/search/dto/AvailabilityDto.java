package com.railway.search.dto;

public class AvailabilityDto {
    private Long trainId;
    private long availableSeats;
    private String status;

    public AvailabilityDto() {}

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public long getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(long availableSeats) { this.availableSeats = availableSeats; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}