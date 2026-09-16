package com.railway.inventoryquota.dto;

import java.util.List;

public class SeatConfirmResponse {
    private boolean success;
    private String pnr;
    private Long reservationId;
    private List<String> confirmedSeats;
    private String message;

    public SeatConfirmResponse() {}

    public SeatConfirmResponse(boolean success, String pnr, Long reservationId, List<String> confirmedSeats, String message) {
        this.success = success;
        this.pnr = pnr;
        this.reservationId = reservationId;
        this.confirmedSeats = confirmedSeats;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public List<String> getConfirmedSeats() { return confirmedSeats; }
    public void setConfirmedSeats(List<String> confirmedSeats) { this.confirmedSeats = confirmedSeats; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}