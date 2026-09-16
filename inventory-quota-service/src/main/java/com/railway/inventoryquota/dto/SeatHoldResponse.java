package com.railway.inventoryquota.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SeatHoldResponse {
    private boolean success;
    private String holdReference;
    private List<Long> heldSeatIds;
    private List<String> seatNumbers;
    private List<String> berthTypes;
    private LocalDateTime expiryTime;
    private String message;

    public SeatHoldResponse() {}

    public SeatHoldResponse(boolean success, String holdReference, List<Long> heldSeatIds, List<String> seatNumbers, List<String> berthTypes, LocalDateTime expiryTime, String message) {
        this.success = success;
        this.holdReference = holdReference;
        this.heldSeatIds = heldSeatIds;
        this.seatNumbers = seatNumbers;
        this.berthTypes = berthTypes;
        this.expiryTime = expiryTime;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getHoldReference() { return holdReference; }
    public void setHoldReference(String holdReference) { this.holdReference = holdReference; }

    public List<Long> getHeldSeatIds() { return heldSeatIds; }
    public void setHeldSeatIds(List<Long> heldSeatIds) { this.heldSeatIds = heldSeatIds; }

    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }

    public List<String> getBerthTypes() { return berthTypes; }
    public void setBerthTypes(List<String> berthTypes) { this.berthTypes = berthTypes; }

    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}