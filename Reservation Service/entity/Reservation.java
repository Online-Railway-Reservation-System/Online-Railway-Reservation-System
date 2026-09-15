package com.railway.reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_res_pnr", columnList = "pnr"),
        @Index(name = "idx_res_cust", columnList = "customer_id")
})
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    @Column(name = "train_id", nullable = false)
    private Long trainId;

    @Column(name = "train_number", nullable = false, length = 20)
    private String trainNumber;

    @Column(name = "train_name", nullable = false, length = 100)
    private String trainName;

    @Column(name = "source_station_code", nullable = false, length = 10)
    private String sourceStationCode;

    @Column(name = "destination_station_code", nullable = false, length = 10)
    private String destinationStationCode;

    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;

    @Column(name = "class_type", nullable = false, length = 10)
    private String classType;

    @Column(nullable = false, length = 20)
    private String quota = "GENERAL";

    @Column(unique = true, length = 20)
    private String pnr;

    @Column(name = "passenger_count", nullable = false)
    private Integer passengerCount;

    @Column(name = "base_fare")
    private Double baseFare = 0.0;

    @Column(name = "concession_discount")
    private Double concessionDiscount = 0.0;

    @Column(name = "reservation_fee")
    private Double reservationFee = 0.0;

    @Column(name = "ticket_fare", nullable = false)
    private Double ticketFare;

    @Column(name = "food_total", nullable = false)
    private Double foodTotal = 0.0;

    @Column(name = "final_amount", nullable = false)
    private Double finalAmount;

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, HELD, CONFIRMED, WAITLISTED, CANCELLED, FAILED

    @Column(name = "hold_reference", length = 100)
    private String holdReference;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Reservation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHoldReference() { return holdReference; }
    public void setHoldReference(String holdReference) { this.holdReference = holdReference; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}