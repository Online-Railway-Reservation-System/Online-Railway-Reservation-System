package com.railway.customer.dto;

public class ConcessionVerificationResponse {
    private boolean eligible;
    private String concessionType;
    private String concessionNumber;
    private String verificationStatus;
    private String message;

    public ConcessionVerificationResponse() {}

    public ConcessionVerificationResponse(boolean eligible, String concessionType, String concessionNumber, String verificationStatus, String message) {
        this.eligible = eligible;
        this.concessionType = concessionType;
        this.concessionNumber = concessionNumber;
        this.verificationStatus = verificationStatus;
        this.message = message;
    }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public String getConcessionType() { return concessionType; }
    public void setConcessionType(String concessionType) { this.concessionType = concessionType; }

    public String getConcessionNumber() { return concessionNumber; }
    public void setConcessionNumber(String concessionNumber) { this.concessionNumber = concessionNumber; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}