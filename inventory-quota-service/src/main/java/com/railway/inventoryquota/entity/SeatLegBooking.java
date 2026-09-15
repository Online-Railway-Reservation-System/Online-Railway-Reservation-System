package com.railway.inventoryquota.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat_leg_bookings",
        indexes = {
                @Index(name = "idx_leg_train_date_class", columnList = "train_id,journey_date,class_type,quota_type,status"),
                @Index(name = "idx_leg_seat", columnList = "train_id,journey_date,coach_number,seat_number"),
                @Index(name = "idx_leg_hold_ref", columnList = "hold_reference"),
                @Index(name = "idx_leg_pnr", columnList = "pnr")
        })
public class SeatLegBooking {

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
    private String seatNumber;

    @Column(name = "class_type", nullable = false, length = 10)
    private String classType;

    @Column(name = "quota_type", nullable = false, length = 20)
    private String quotaType;

    @Column(name = "from_station_code", length = 10)
    private String fromStationCode;

    @Column(name = "to_station_code", length = 10)
    private String toStationCode;

    @Column(name = "from_stop_seq", nullable = false)
    private Integer fromStopSeq;

    @Column(name = "to_stop_seq", nullable = false)
    private Integer toStopSeq;

    @Column(nullable = false, length = 20)
    private String status = "HELD"; // HELD, CONFIRMED, CANCELLED

    @Column(name = "hold_reference", length = 100)
    private String holdReference;

    @Column(name = "hold_expiry")
    private LocalDateTime holdExpiry;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(length = 20)
    private String pnr;

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

    public SeatLegBooking() {}

    public SeatLegBooking(Long trainId, LocalDate journeyDate, String coachNumber, String seatNumber,
                          String classType, String quotaType, String fromStationCode, String toStationCode,
                          Integer fromStopSeq, Integer toStopSeq, String status, String holdReference,
                          LocalDateTime holdExpiry) {
        this.trainId = trainId;
        this.journeyDate = journeyDate;
        this.coachNumber = coachNumber;
        this.seatNumber = seatNumber;
        this.classType = classType;
        this.quotaType = quotaType;
        this.fromStationCode = fromStationCode;
        this.toStationCode = toStationCode;
        this.fromStopSeq = fromStopSeq;
        this.toStopSeq = toStopSeq;
        this.status = status;
        this.holdReference = holdReference;
        this.holdExpiry = holdExpiry;
    }

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

    public String getQuotaType() { return quotaType; }
    public void setQuotaType(String quotaType) { this.quotaType = quotaType; }

    public String getFromStationCode() { return fromStationCode; }
    public void setFromStationCode(String fromStationCode) { this.fromStationCode = fromStationCode; }

    public String getToStationCode() { return toStationCode; }
    public void setToStationCode(String toStationCode) { this.toStationCode = toStationCode; }

    public Integer getFromStopSeq() { return fromStopSeq; }
    public void setFromStopSeq(Integer fromStopSeq) { this.fromStopSeq = fromStopSeq; }

    public Integer getToStopSeq() { return toStopSeq; }
    public void setToStopSeq(Integer toStopSeq) { this.toStopSeq = toStopSeq; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
