package com.railway.inventoryquota.dto;

import java.util.List;

public class SeatReleaseRequest {
    private String holdReference;
    private String pnr;
    private List<Long> seatIds;

    public SeatReleaseRequest() {}

    public SeatReleaseRequest(String holdReference, String pnr, List<Long> seatIds) {
        this.holdReference = holdReference;
        this.pnr = pnr;
        this.seatIds = seatIds;
    }

    public String getHoldReference() { return holdReference; }
    public void setHoldReference(String holdReference) { this.holdReference = holdReference; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public List<Long> getSeatIds() { return seatIds; }
    public void setSeatIds(List<Long> seatIds) { this.seatIds = seatIds; }
}