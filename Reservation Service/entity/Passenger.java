package com.railway.reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "passengers", indexes = {
        @Index(name = "idx_pass_res_id", columnList = "reservation_id")
})
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(nullable = false)
    private Integer age;

    @Column(length = 255)
    private String address;

    @Column(name = "seat_preference", length = 20)
    private String seatPreference = "NO_PREFERENCE"; // LOWER, MIDDLE, UPPER, SIDE_LOWER, SIDE_UPPER, NO_PREFERENCE

    @Column(name = "seat_number", length = 20)
    private String seatNumber;

    @Column(name = "berth_type", length = 20)
    private String berthType;

    @Column(nullable = false, length = 20)
    private String status = "CNF"; // CNF, RAC, WL

    @Column(name = "concession_type", length = 50)
    private String concessionType;

    @Column(name = "concession_number", length = 50)
    private String concessionNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Passenger() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getSeatPreference() { return seatPreference; }
    public void setSeatPreference(String seatPreference) { this.seatPreference = seatPreference; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getBerthType() { return berthType; }
    public void setBerthType(String berthType) { this.berthType = berthType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getConcessionType() { return concessionType; }
    public void setConcessionType(String concessionType) { this.concessionType = concessionType; }

    public String getConcessionNumber() { return concessionNumber; }
    public void setConcessionNumber(String concessionNumber) { this.concessionNumber = concessionNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}