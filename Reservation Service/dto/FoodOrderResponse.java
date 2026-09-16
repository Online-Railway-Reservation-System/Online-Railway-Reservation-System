package com.railway.reservation.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FoodOrderResponse {
    private Long id;
    private Long reservationId;
    private String pnr;
    private Double totalAmount;
    private String status;
    private List<FoodOrderItemDto> items;
    private LocalDateTime createdAt;

    public FoodOrderResponse() {}

    public FoodOrderResponse(Long id, Long reservationId, String pnr, Double totalAmount, String status, List<FoodOrderItemDto> items, LocalDateTime createdAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.pnr = pnr;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<FoodOrderItemDto> getItems() { return items; }
    public void setItems(List<FoodOrderItemDto> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class FoodOrderItemDto {
        private Long foodMenuId;
        private String itemName;
        private Integer quantity;
        private Double unitPrice;
        private Double subtotal;

        public FoodOrderItemDto() {}

        public FoodOrderItemDto(Long foodMenuId, String itemName, Integer quantity, Double unitPrice, Double subtotal) {
            this.foodMenuId = foodMenuId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
        }

        public Long getFoodMenuId() { return foodMenuId; }
        public void setFoodMenuId(Long foodMenuId) { this.foodMenuId = foodMenuId; }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

        public Double getSubtotal() { return subtotal; }
        public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    }
}