package com.railway.customer.dto;

import jakarta.validation.constraints.NotBlank;

public class ConcessionRequest {
    @NotBlank(message = "Concession type is required")
    private String concessionType;

    @NotBlank(message = "Concession number is required")
    private String concessionNumber;

    public ConcessionRequest() {}

    public String getConcessionType() { return concessionType; }
    public void setConcessionType(String concessionType) { this.concessionType = concessionType; }

    public String getConcessionNumber() { return concessionNumber; }
    public void setConcessionNumber(String concessionNumber) { this.concessionNumber = concessionNumber; }
}