package com.railway.search.dto;

public class TrainSearchResult {
    private Long trainId;
    private String trainNumber;
    private String trainName;
    private String trainType;
    private String source;
    private String destination;
    private String departure;
    private String arrival;
    private String duration;
    private Double distanceKm;
    private String classType;
    private String quota;
    private Double fare;
    private long availableSeats;
    private String status;
    private boolean bookingOpen = true;
    private boolean departed = false;
    private String nextAvailableDate;
    private Long nextDayAvailableSeats;
    private boolean tatkalAvailable = true;
    private String tatkalOpeningTime;
    private boolean tatkalWindowOpen = true;
    private String tatkalWindowStatus = "OPEN";
    private String tatkalWindowMessage;

    public TrainSearchResult() {}

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }

    public String getTrainType() { return trainType; }
    public void setTrainType(String trainType) { this.trainType = trainType; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }

    public String getArrival() { return arrival; }
    public void setArrival(String arrival) { this.arrival = arrival; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public String getQuota() { return quota; }
    public void setQuota(String quota) { this.quota = quota; }

    public Double getFare() { return fare; }
    public void setFare(Double fare) { this.fare = fare; }

    public long getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(long availableSeats) { this.availableSeats = availableSeats; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isBookingOpen() { return bookingOpen; }
    public void setBookingOpen(boolean bookingOpen) { this.bookingOpen = bookingOpen; }

    public boolean isDeparted() { return departed; }
    public void setDeparted(boolean departed) { this.departed = departed; }

    public String getNextAvailableDate() { return nextAvailableDate; }
    public void setNextAvailableDate(String nextAvailableDate) { this.nextAvailableDate = nextAvailableDate; }

    public Long getNextDayAvailableSeats() { return nextDayAvailableSeats; }
    public void setNextDayAvailableSeats(Long nextDayAvailableSeats) { this.nextDayAvailableSeats = nextDayAvailableSeats; }

    public boolean isTatkalAvailable() { return tatkalAvailable; }
    public void setTatkalAvailable(boolean tatkalAvailable) { this.tatkalAvailable = tatkalAvailable; }

    public String getTatkalOpeningTime() { return tatkalOpeningTime; }
    public void setTatkalOpeningTime(String tatkalOpeningTime) { this.tatkalOpeningTime = tatkalOpeningTime; }

    public boolean isTatkalWindowOpen() { return tatkalWindowOpen; }
    public void setTatkalWindowOpen(boolean tatkalWindowOpen) { this.tatkalWindowOpen = tatkalWindowOpen; }

    public String getTatkalWindowStatus() { return tatkalWindowStatus; }
    public void setTatkalWindowStatus(String tatkalWindowStatus) { this.tatkalWindowStatus = tatkalWindowStatus; }

    public String getTatkalWindowMessage() { return tatkalWindowMessage; }
    public void setTatkalWindowMessage(String tatkalWindowMessage) { this.tatkalWindowMessage = tatkalWindowMessage; }
}