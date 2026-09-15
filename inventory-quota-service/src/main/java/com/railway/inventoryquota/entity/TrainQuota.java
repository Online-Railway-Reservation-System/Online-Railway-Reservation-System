package com.railway.inventoryquota.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "train_quotas")
public class TrainQuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_id", nullable = false)
    private Long trainId;

    @Column(name = "class_type", nullable = false, length = 10)
    private String classType;

    @Column(name = "quota_type", nullable = false, length = 20)
    private String quotaType; // GENERAL, TATKAL, LADIES

    @Column(name = "allocated_seats", nullable = false)
    private Integer allocatedSeats;

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

    public TrainQuota() {}

    public TrainQuota(Long id, Long trainId, String classType, String quotaType, Integer allocatedSeats) {
        this.id = id;
        this.trainId = trainId;
        this.classType = classType;
        this.quotaType = quotaType;
        this.allocatedSeats = allocatedSeats;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public String getQuotaType() { return quotaType; }
    public void setQuotaType(String quotaType) { this.quotaType = quotaType; }

    public Integer getAllocatedSeats() { return allocatedSeats; }
    public void setAllocatedSeats(Integer allocatedSeats) { this.allocatedSeats = allocatedSeats; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}