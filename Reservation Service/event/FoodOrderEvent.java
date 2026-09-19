package com.railway.reservation.event;

import java.time.LocalDateTime;

public class FoodOrderEvent {
    private String eventType; // FOOD_ORDER_CREATED, FOOD_ORDER_CONFIRMED, FOOD_ORDER_CANCELLED
    private Long foodOrderId;
    private Long reservationId;
    private String pnr;
    private Double totalAmount;
    private String status;
    private LocalDateTime timestamp;

    public FoodOrderEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public FoodOrderEvent(String eventType, Long foodOrderId, Long reservationId, String pnr, Double totalAmount, String status) {
        this.eventType = eventType;
        this.foodOrderId = foodOrderId;
        this.reservationId = reservationId;
        this.pnr = pnr;
        this.totalAmount = totalAmount;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getFoodOrderId() { return foodOrderId; }
    public void setFoodOrderId(Long foodOrderId) { this.foodOrderId = foodOrderId; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}