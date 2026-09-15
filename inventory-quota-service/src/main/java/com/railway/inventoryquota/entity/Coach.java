package com.railway.inventoryquota.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coaches")
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_id", nullable = false)
    private Long trainId;

    @Column(name = "coach_number", nullable = false, length = 10)
    private String coachNumber; // S1, S2, B1, A1, H1, D1

    @Column(name = "class_type", nullable = false, length = 10)
    private String classType; // SL, 3A, 2A, 1A, CC, 2S

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats = 72;

    @Column(name = "active_status", nullable = false)
    private boolean activeStatus = true;

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

    public Coach() {}

    public Coach(Long id, Long trainId, String coachNumber, String classType, Integer totalSeats, boolean activeStatus) {
        this.id = id;
        this.trainId = trainId;
        this.coachNumber = coachNumber;
        this.classType = classType;
        this.totalSeats = totalSeats;
        this.activeStatus = activeStatus;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public String getCoachNumber() { return coachNumber; }
    public void setCoachNumber(String coachNumber) { this.coachNumber = coachNumber; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public boolean isActiveStatus() { return activeStatus; }
    public void setActiveStatus(boolean activeStatus) { this.activeStatus = activeStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}