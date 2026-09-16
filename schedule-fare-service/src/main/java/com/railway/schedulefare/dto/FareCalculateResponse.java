package com.railway.schedulefare.dto;

public class FareCalculateResponse {
    private Long trainId;
    private String classType;
    private String quota;
    private Double baseFare;
    private Double reservationFee;
    private Double superfastCharge;
    private Double quotaSurcharge;
    private Double concessionDiscount;
    private Double farePerPassenger;
    private Integer passengerCount;
    private Double finalPayableFare;
    private String breakdown;

    public FareCalculateResponse() {}

    public FareCalculateResponse(Long trainId, String classType, String quota, Double baseFare, Double reservationFee, Double superfastCharge, Double quotaSurcharge, Double concessionDiscount, Double farePerPassenger, Integer passengerCount, Double finalPayableFare, String breakdown) {
        this.trainId = trainId;
        this.classType = classType;
        this.quota = quota;
        this.baseFare = baseFare;
        this.reservationFee = reservationFee;
        this.superfastCharge = superfastCharge;
        this.quotaSurcharge = quotaSurcharge;
        this.concessionDiscount = concessionDiscount;
        this.farePerPassenger = farePerPassenger;
        this.passengerCount = passengerCount;
        this.finalPayableFare = finalPayableFare;
        this.breakdown = breakdown;
    }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public String getQuota() { return quota; }
    public void setQuota(String quota) { this.quota = quota; }

    public Double getBaseFare() { return baseFare; }
    public void setBaseFare(Double baseFare) { this.baseFare = baseFare; }

    public Double getReservationFee() { return reservationFee; }
    public void setReservationFee(Double reservationFee) { this.reservationFee = reservationFee; }

    public Double getSuperfastCharge() { return superfastCharge; }
    public void setSuperfastCharge(Double superfastCharge) { this.superfastCharge = superfastCharge; }

    public Double getQuotaSurcharge() { return quotaSurcharge; }
    public void setQuotaSurcharge(Double quotaSurcharge) { this.quotaSurcharge = quotaSurcharge; }

    public Double getConcessionDiscount() { return concessionDiscount; }
    public void setConcessionDiscount(Double concessionDiscount) { this.concessionDiscount = concessionDiscount; }

    public Double getFarePerPassenger() { return farePerPassenger; }
    public void setFarePerPassenger(Double farePerPassenger) { this.farePerPassenger = farePerPassenger; }

    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }

    public Double getFinalPayableFare() { return finalPayableFare; }
    public void setFinalPayableFare(Double finalPayableFare) { this.finalPayableFare = finalPayableFare; }

    public String getBreakdown() { return breakdown; }
    public void setBreakdown(String breakdown) { this.breakdown = breakdown; }
}