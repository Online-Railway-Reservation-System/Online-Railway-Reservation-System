package com.railway.reservation.event;

import java.time.LocalDateTime;

public class ReservationEvent {
    private String eventType; // RESERVATION_CREATED, RESERVATION_CONFIRMED, RESERVATION_CANCELLED, PAYMENT_FAILED
    private Long reservationId;
    private String pnr;
    private Long customerId;
    private String customerEmail;
    private String trainNumber;
    private Double totalAmount;
    private String status;
    private String message;
    private LocalDateTime timestamp;

    public ReservationEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public ReservationEvent(String eventType, Long reservationId, String pnr, Long customerId, String trainNumber, Double totalAmount, String status, String message) {
        this.eventType = eventType;
        this.reservationId = reservationId;
        this.pnr = pnr;
        this.customerId = customerId;
        this.trainNumber = trainNumber;
        this.totalAmount = totalAmount;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ReservationEvent(String eventType, Long reservationId, String pnr, Long customerId, String customerEmail, String trainNumber, Double totalAmount, String status, String message) {
        this.eventType = eventType;
        this.reservationId = reservationId;
        this.pnr = pnr;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.trainNumber = trainNumber;
        this.totalAmount = totalAmount;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}