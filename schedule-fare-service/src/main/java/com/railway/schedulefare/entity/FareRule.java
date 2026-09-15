package com.railway.schedulefare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fare_rules")
public class FareRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_id")
    private Long trainId; // Nullable for generic class rules

    @Column(name = "class_type", nullable = false, length = 10)
    private String classType; // 1A, 2A, 3A, SL, CC, 2S

    @Column(name = "base_fare_per_km", nullable = false)
    private Double baseFarePerKm;

    @Column(name = "minimum_fare", nullable = false)
    private Double minimumFare;

    @Column(name = "reservation_fee", nullable = false)
    private Double reservationFee;

    @Column(name = "superfast_charge", nullable = false)
    private Double superfastCharge;

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

    public FareRule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public Double getBaseFarePerKm() { return baseFarePerKm; }
    public void setBaseFarePerKm(Double baseFarePerKm) { this.baseFarePerKm = baseFarePerKm; }

    public Double getMinimumFare() { return minimumFare; }
    public void setMinimumFare(Double minimumFare) { this.minimumFare = minimumFare; }

    public Double getReservationFee() { return reservationFee; }
    public void setReservationFee(Double reservationFee) { this.reservationFee = reservationFee; }

    public Double getSuperfastCharge() { return superfastCharge; }
    public void setSuperfastCharge(Double superfastCharge) { this.superfastCharge = superfastCharge; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}