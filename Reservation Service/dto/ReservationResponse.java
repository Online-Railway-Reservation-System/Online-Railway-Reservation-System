package com.railway.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationResponse {
    private Long id;
    private String pnr;
    private Long customerId;
    private String customerEmail;
    private Long trainId;
    private String trainNumber;
    private String trainName;
    private String sourceStationCode;
    private String destinationStationCode;
    private LocalDate journeyDate;
    private String classType;
    private String quota;
    private Integer passengerCount;
    private Double baseFare;
    private Double concessionDiscount;
    private Double reservationFee;
    private Double ticketFare;
    private Double foodTotal;
    private Double finalAmount;
    private String status;
    private Long paymentId;
    private List<PassengerResponse> passengers;
    private FoodOrderResponse foodOrder;
    private LocalDateTime createdAt;

    public ReservationResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }

    public String getSourceStationCode() { return sourceStationCode; }
    public void setSourceStationCode(String sourceStationCode) { this.sourceStationCode = sourceStationCode; }

    public String getDestinationStationCode() { return destinationStationCode; }
    public void setDestinationStationCode(String destinationStationCode) { this.destinationStationCode = destinationStationCode; }

    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public String getQuota() { return quota; }
    public void setQuota(String quota) { this.quota = quota; }

    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }

    public Double getBaseFare() { return baseFare; }
    public void setBaseFare(Double baseFare) { this.baseFare = baseFare; }

    public Double getConcessionDiscount() { return concessionDiscount; }
    public void setConcessionDiscount(Double concessionDiscount) { this.concessionDiscount = concessionDiscount; }

    public Double getReservationFee() { return reservationFee; }
    public void setReservationFee(Double reservationFee) { this.reservationFee = reservationFee; }

    public Double getTicketFare() { return ticketFare; }
    public void setTicketFare(Double ticketFare) { this.ticketFare = ticketFare; }

    public Double getFoodTotal() { return foodTotal; }
    public void setFoodTotal(Double foodTotal) { this.foodTotal = foodTotal; }

    public Double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }

    public Double getTotalFare() { return finalAmount != null ? finalAmount : ticketFare; }
    public void setTotalFare(Double totalFare) { if (this.finalAmount == null) this.finalAmount = totalFare; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public List<PassengerResponse> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerResponse> passengers) { this.passengers = passengers; }

    public FoodOrderResponse getFoodOrder() { return foodOrder; }
    public void setFoodOrder(FoodOrderResponse foodOrder) { this.foodOrder = foodOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}