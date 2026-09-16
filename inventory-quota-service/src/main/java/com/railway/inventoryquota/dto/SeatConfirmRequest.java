package com.railway.inventoryquota.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SeatConfirmRequest {

    @NotBlank(message = "Hold reference is required")
    private String holdReference;

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotBlank(message = "PNR is required")
    private String pnr;

    public SeatConfirmRequest() {}

    public SeatConfirmRequest(String holdReference, Long reservationId, String pnr) {
        this.holdReference = holdReference;
        this.reservationId = reservationId;
        this.pnr = pnr;
    }

    public String getHoldReference() { return holdReference; }
    public void setHoldReference(String holdReference) { this.holdReference = holdReference; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }
}