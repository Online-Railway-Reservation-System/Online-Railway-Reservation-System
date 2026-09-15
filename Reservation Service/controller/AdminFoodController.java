package com.railway.reservation.controller;

import com.railway.reservation.dto.ApiResponse;
import com.railway.reservation.dto.FoodMenuRequest;
import com.railway.reservation.dto.FoodMenuResponse;
import com.railway.reservation.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/food")
@Tag(name = "Admin Catering Management", description = "Food menu catalog maintenance, pricing, and availability")
public class AdminFoodController {

    private final FoodService foodService;

    public AdminFoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @PostMapping("/menu")
    @Operation(summary = "Add a new dish/refreshment to the catering menu (Admin)")
    public ResponseEntity<ApiResponse<FoodMenuResponse>> createMenuItem(@Valid @RequestBody FoodMenuRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(foodService.createMenuItem(request)));
    }

    @PutMapping("/menu/{id}")
    @Operation(summary = "Update food menu item details and pricing (Admin)")
    public ResponseEntity<ApiResponse<FoodMenuResponse>> updateMenuItem(@PathVariable Long id, @Valid @RequestBody FoodMenuRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Menu item updated successfully", foodService.updateMenuItem(id, request)));
    }

    @DeleteMapping("/menu/{id}")
    @Operation(summary = "Disable/soft-delete food menu item (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(@PathVariable Long id) {
        foodService.deleteMenuItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Menu item removed successfully", null));
    }

    @PatchMapping("/menu/{id}/availability")
    @Operation(summary = "Toggle food menu item availability (Admin)")
    public ResponseEntity<ApiResponse<FoodMenuResponse>> updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        return ResponseEntity.ok(ApiResponse.ok("Menu item availability updated", foodService.updateAvailability(id, available)));
    }
}
