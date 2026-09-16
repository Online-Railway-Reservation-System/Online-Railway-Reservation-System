package com.railway.inventoryquota.dto;

public class SeatReleaseResponse {
    private boolean success;
    private int releasedCount;
    private String message;

    public SeatReleaseResponse() {}

    public SeatReleaseResponse(boolean success, int releasedCount, String message) {
        this.success = success;
        this.releasedCount = releasedCount;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public int getReleasedCount() { return releasedCount; }
    public void setReleasedCount(int releasedCount) { this.releasedCount = releasedCount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}