package com.railway.reservation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationRequest {

    private Long customerId;
    private String customerEmail;

    @NotNull(message = "Train ID is required")
    private Long trainId;

    @NotBlank(message = "Source station code is required")
    private String sourceStationCode;

    @NotBlank(message = "Destination station code is required")
    private String destinationStationCode;

    @NotNull(message = "Journey date is required")
    private LocalDate journeyDate;

    @NotBlank(message = "Class type is required")
    private String classType;

    private String quota = "GENERAL";

    @NotEmpty(message = "At least one passenger is required")
    @Size(max = 6, message = "Maximum 6 passengers allowed per booking")
    @Valid
    private List<PassengerRequest> passengers = new ArrayList<>();

    @Valid
    private List<FoodItemSelection> foodItems = new ArrayList<>();

    private String paymentMethod = "CREDIT_CARD"; // CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING

    public ReservationRequest() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

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

    public List<PassengerRequest> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerRequest> passengers) { this.passengers = passengers; }

    public List<FoodItemSelection> getFoodItems() { return foodItems; }
    public void setFoodItems(List<FoodItemSelection> foodItems) { this.foodItems = foodItems; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}