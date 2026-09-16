package com.railway.reservation.controller;

import com.railway.reservation.dto.ApiResponse;
import com.railway.reservation.dto.FoodMenuResponse;
import com.railway.reservation.dto.FoodOrderRequest;
import com.railway.reservation.dto.FoodOrderResponse;
import com.railway.reservation.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/food")
@Tag(name = "Food and Catering", description = "Train meal menu, passenger catering orders, and meal cancellations")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/menu")
    @Operation(summary = "Get current available catering menu items")
    public ResponseEntity<ApiResponse<List<FoodMenuResponse>>> getAvailableMenu() {
        return ResponseEntity.ok(ApiResponse.ok(foodService.getAvailableMenu()));
    }

    @GetMapping("/menu/{id}")
    @Operation(summary = "Get food menu item details by ID")
    public ResponseEntity<ApiResponse<FoodMenuResponse>> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(foodService.getMenuItemById(id)));
    }

    @GetMapping("/order/{reservationId}")
    @Operation(summary = "Get catering order details for a reservation")
    public ResponseEntity<ApiResponse<FoodOrderResponse>> getFoodOrderByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.ok(foodService.getFoodOrderByReservationId(reservationId)));
    }

    @PostMapping("/order/{reservationId}")
    @Operation(summary = "Order meals/refreshments for a confirmed reservation")
    public ResponseEntity<ApiResponse<FoodOrderResponse>> orderFood(
            @PathVariable Long reservationId,
            @Valid @RequestBody FoodOrderRequest request) {
        FoodOrderResponse response = foodService.orderFoodForReservation(reservationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PostMapping("/order/{reservationId}/cancel/{foodOrderId}")
    @Operation(summary = "Cancel catering meal order for a reservation")
    public ResponseEntity<ApiResponse<FoodOrderResponse>> cancelFoodOrder(
            @PathVariable Long reservationId,
            @PathVariable Long foodOrderId) {
        return ResponseEntity.ok(ApiResponse.ok("Meal order cancelled successfully", foodService.cancelFoodOrder(reservationId, foodOrderId)));
    }
}
