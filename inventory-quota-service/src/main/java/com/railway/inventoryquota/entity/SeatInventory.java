package com.railway.inventoryquota.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat_inventory",
        indexes = {
                @Index(name = "idx_train_date_class_quota", columnList = "train_id,journey_date,class_type,quota_type,status"),
                @Index(name = "idx_hold_ref", columnList = "hold_reference"),
                @Index(name = "idx_pnr", columnList = "pnr")
        })
public class SeatInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_id", nullable = false)
    private Long trainId;

    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;

    @Column(name = "coach_number", nullable = false, length = 10)
    private String coachNumber;

    @Column(name = "seat_number", nullable = false, length = 20)
    private String seatNumber; // e.g. "S1-12"

    @Column(name = "class_type", nullable = false, length = 10)
    private String classType;

    @Column(name = "berth_type", nullable = false, length = 20)
    private String berthType; // LOWER, MIDDLE, UPPER, SIDE_LOWER, SIDE_UPPER

    @Column(name = "quota_type", nullable = false, length = 20)
    private String quotaType; // GENERAL, TATKAL, LADIES

    @Column(nullable = false, length = 20)
    private String status = "AVAILABLE"; // AVAILABLE, HELD, CONFIRMED, CANCELLED

    @Column(name = "hold_reference", length = 100)
    private String holdReference;

    @Column(name = "hold_expiry")
    private LocalDateTime holdExpiry;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(length = 20)
    private String pnr;

    @Version
    private Long version;

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

    public SeatInventory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }

    public String getCoachNumber() { return coachNumber; }
    public void setCoachNumber(String coachNumber) { this.coachNumber = coachNumber; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public String getBerthType() { return berthType; }
    public void setBerthType(String berthType) { this.berthType = berthType; }

    public String getQuotaType() { return quotaType; }
    public void setQuotaType(String quotaType) { this.quotaType = quotaType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHoldReference() { return holdReference; }
    public void setHoldReference(String holdReference) { this.holdReference = holdReference; }

    public LocalDateTime getHoldExpiry() { return holdExpiry; }
    public void setHoldExpiry(LocalDateTime holdExpiry) { this.holdExpiry = holdExpiry; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Long getVersion() { return version; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}