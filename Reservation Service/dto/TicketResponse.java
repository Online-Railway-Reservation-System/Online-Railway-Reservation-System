package com.railway.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TicketResponse {
    private Long id;
    private Long reservationId;
    private String pnr;
    private String trainNumber;
    private String trainName;
    private LocalDate journeyDate;
    private String sourceStation;
    private String destinationStation;
    private String departureTime;
    private String arrivalTime;
    private Double baseFare;
    private Double concessionDiscount;
    private Double reservationFee;
    private Double foodTotal;
    private Double totalAmount;
    private String status;
    private List<PassengerResponse> passengers;
    private LocalDateTime bookedAt;

    public TicketResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }

    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }

    public String getSourceStation() { return sourceStation; }
    public void setSourceStation(String sourceStation) { this.sourceStation = sourceStation; }

    public String getDestinationStation() { return destinationStation; }
    public void setDestinationStation(String destinationStation) { this.destinationStation = destinationStation; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public Double getBaseFare() { return baseFare; }
    public void setBaseFare(Double baseFare) { this.baseFare = baseFare; }

    public Double getConcessionDiscount() { return concessionDiscount; }
    public void setConcessionDiscount(Double concessionDiscount) { this.concessionDiscount = concessionDiscount; }

    public Double getReservationFee() { return reservationFee; }
    public void setReservationFee(Double reservationFee) { this.reservationFee = reservationFee; }

    public Double getFoodTotal() { return foodTotal; }
    public void setFoodTotal(Double foodTotal) { this.foodTotal = foodTotal; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<PassengerResponse> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerResponse> passengers) { this.passengers = passengers; }

    public LocalDateTime getBookedAt() { return bookedAt; }
    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }
}