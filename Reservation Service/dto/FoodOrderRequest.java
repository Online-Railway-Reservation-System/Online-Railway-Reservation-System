package com.railway.reservation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class FoodOrderRequest {

    @NotEmpty(message = "At least one food item must be selected")
    @Valid
    private List<FoodItemSelection> items = new ArrayList<>();

    public FoodOrderRequest() {}

    public List<FoodItemSelection> getItems() { return items; }
    public void setItems(List<FoodItemSelection> items) { this.items = items; }
}