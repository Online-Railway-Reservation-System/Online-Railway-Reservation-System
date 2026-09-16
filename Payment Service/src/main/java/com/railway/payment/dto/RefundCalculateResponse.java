package com.railway.payment.dto;

public class RefundCalculateResponse {
    private Double originalAmount;
    private Long hoursBeforeDeparture;
    private Double cancellationCharge;
    private Double refundAmount;
    private Integer refundPercentage;
    private String policyDescription;

    public RefundCalculateResponse() {}

    public RefundCalculateResponse(Double originalAmount, Long hoursBeforeDeparture, Double cancellationCharge, Double refundAmount, Integer refundPercentage, String policyDescription) {
        this.originalAmount = originalAmount;
        this.hoursBeforeDeparture = hoursBeforeDeparture;
        this.cancellationCharge = cancellationCharge;
        this.refundAmount = refundAmount;
        this.refundPercentage = refundPercentage;
        this.policyDescription = policyDescription;
    }

    public Double getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(Double originalAmount) { this.originalAmount = originalAmount; }

    public Long getHoursBeforeDeparture() { return hoursBeforeDeparture; }
    public void setHoursBeforeDeparture(Long hoursBeforeDeparture) { this.hoursBeforeDeparture = hoursBeforeDeparture; }

    public Double getCancellationCharge() { return cancellationCharge; }
    public void setCancellationCharge(Double cancellationCharge) { this.cancellationCharge = cancellationCharge; }

    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }

    public Integer getRefundPercentage() { return refundPercentage; }
    public void setRefundPercentage(Integer refundPercentage) { this.refundPercentage = refundPercentage; }

    public String getPolicyDescription() { return policyDescription; }
    public void setPolicyDescription(String policyDescription) { this.policyDescription = policyDescription; }
}
