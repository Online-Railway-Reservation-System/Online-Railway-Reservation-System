package com.railway.reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "food_order_items", indexes = {
        @Index(name = "idx_food_order_item_order", columnList = "food_order_id")
})
public class FoodOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "food_order_id", nullable = false)
    private Long foodOrderId;

    @Column(name = "food_menu_id", nullable = false)
    private Long foodMenuId;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(nullable = false)
    private Double subtotal;

    public FoodOrderItem() {}

    public FoodOrderItem(Long id, Long foodOrderId, Long foodMenuId, String itemName, Integer quantity, Double unitPrice, Double subtotal) {
        this.id = id;
        this.foodOrderId = foodOrderId;
        this.foodMenuId = foodMenuId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFoodOrderId() { return foodOrderId; }
    public void setFoodOrderId(Long foodOrderId) { this.foodOrderId = foodOrderId; }

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