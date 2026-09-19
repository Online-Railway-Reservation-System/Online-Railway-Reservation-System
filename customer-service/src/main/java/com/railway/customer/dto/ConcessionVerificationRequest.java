package com.railway.customer.dto;

import jakarta.validation.constraints.NotBlank;

public class ConcessionVerificationRequest {
    private Long customerId;

    @NotBlank(message = "Concession type is required")
    private String concessionType;

    @NotBlank(message = "Concession number is required")
    private String concessionNumber;

    public ConcessionVerificationRequest() {}

    public ConcessionVerificationRequest(Long customerId, String concessionType, String concessionNumber) {
        this.customerId = customerId;
        this.concessionType = concessionType;
        this.concessionNumber = concessionNumber;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getConcessionType() { return concessionType; }
    public void setConcessionType(String concessionType) { this.concessionType = concessionType; }

    public String getConcessionNumber() { return concessionNumber; }
    public void setConcessionNumber(String concessionNumber) { this.concessionNumber = concessionNumber; }
}