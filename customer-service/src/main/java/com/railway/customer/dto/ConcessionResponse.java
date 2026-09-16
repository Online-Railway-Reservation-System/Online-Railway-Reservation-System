package com.railway.customer.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConcessionResponse {
    private Long id;
    private Long customerId;
    private String concessionType;
    private String concessionNumber;
    private LocalDate validFrom;
    private LocalDate validUpto;
    private String verificationStatus;
    private LocalDateTime verifiedAt;

    public ConcessionResponse() {}

    public ConcessionResponse(Long id, Long customerId, String concessionType, String concessionNumber, LocalDate validFrom, LocalDate validUpto, String verificationStatus, LocalDateTime verifiedAt) {
        this.id = id;
        this.customerId = customerId;
        this.concessionType = concessionType;
        this.concessionNumber = concessionNumber;
        this.validFrom = validFrom;
        this.validUpto = validUpto;
        this.verificationStatus = verificationStatus;
        this.verifiedAt = verifiedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getConcessionType() { return concessionType; }
    public void setConcessionType(String concessionType) { this.concessionType = concessionType; }

    public String getConcessionNumber() { return concessionNumber; }
    public void setConcessionNumber(String concessionNumber) { this.concessionNumber = concessionNumber; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidUpto() { return validUpto; }
    public void setValidUpto(LocalDate validUpto) { this.validUpto = validUpto; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
}