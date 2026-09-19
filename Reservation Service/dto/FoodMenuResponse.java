package com.railway.reservation.dto;

public class FoodMenuResponse {
    private Long id;
    private String itemName;
    private String description;
    private String category;
    private Double price;
    private boolean available;

    public FoodMenuResponse() {}

    public FoodMenuResponse(Long id, String itemName, String description, String category, Double price, boolean available) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.available = available;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}