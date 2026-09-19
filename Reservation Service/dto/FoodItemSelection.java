package com.railway.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class FoodItemSelection {

    @NotNull(message = "Food menu ID is required")
    private Long foodMenuId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public FoodItemSelection() {}

    public FoodItemSelection(Long foodMenuId, Integer quantity) {
        this.foodMenuId = foodMenuId;
        this.quantity = quantity;
    }

    public Long getFoodMenuId() { return foodMenuId; }
    public void setFoodMenuId(Long foodMenuId) { this.foodMenuId = foodMenuId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}