package com.railway.customer.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "concession_verification")
public class ConcessionVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concession_number", nullable = false, unique = true, length = 50)
    private String concessionNumber;

    @Column(name = "concession_type", nullable = false, length = 50)
    private String concessionType;

    @Column(name = "holder_name", nullable = false, length = 100)
    private String holderName;

    @Column(name = "holder_identifier", length = 100)
    private String holderIdentifier;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_upto", nullable = false)
    private LocalDate validUpto;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, SUSPENDED

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

    public ConcessionVerification() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConcessionNumber() { return concessionNumber; }
    public void setConcessionNumber(String concessionNumber) { this.concessionNumber = concessionNumber; }

    public String getConcessionType() { return concessionType; }
    public void setConcessionType(String concessionType) { this.concessionType = concessionType; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public String getHolderIdentifier() { return holderIdentifier; }
    public void setHolderIdentifier(String holderIdentifier) { this.holderIdentifier = holderIdentifier; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidUpto() { return validUpto; }
    public void setValidUpto(LocalDate validUpto) { this.validUpto = validUpto; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}