package com.railway.schedulefare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tatkal_config")
public class TatkalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_id", nullable = false, unique = true)
    private Long trainId;

    @Column(name = "tatkal_enabled", nullable = false)
    private boolean tatkalEnabled = true;

    @Column(name = "advance_days", nullable = false)
    private int advanceDays = 1;

    @Column(name = "ac_opening_time", nullable = false, length = 10)
    private String acOpeningTime = "10:00";

    @Column(name = "non_ac_opening_time", nullable = false, length = 10)
    private String nonAcOpeningTime = "11:00";

    @Column(name = "surcharge_percentage", nullable = false)
    private Double surchargePercentage = 30.0;

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

    public TatkalConfig() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public boolean isTatkalEnabled() { return tatkalEnabled; }
    public void setTatkalEnabled(boolean tatkalEnabled) { this.tatkalEnabled = tatkalEnabled; }

    public int getAdvanceDays() { return advanceDays; }
    public void setAdvanceDays(int advanceDays) { this.advanceDays = advanceDays; }

    public String getAcOpeningTime() { return acOpeningTime; }
    public void setAcOpeningTime(String acOpeningTime) { this.acOpeningTime = acOpeningTime; }

    public String getNonAcOpeningTime() { return nonAcOpeningTime; }
    public void setNonAcOpeningTime(String nonAcOpeningTime) { this.nonAcOpeningTime = nonAcOpeningTime; }

    public Double getSurchargePercentage() { return surchargePercentage; }
    public void setSurchargePercentage(Double surchargePercentage) { this.surchargePercentage = surchargePercentage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}