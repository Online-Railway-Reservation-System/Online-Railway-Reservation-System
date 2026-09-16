package com.railway.reservation.service;

import com.railway.reservation.dto.*;
import com.railway.reservation.entity.FoodMenu;

import java.util.List;

public interface FoodService {
    List<FoodMenuResponse> getAvailableMenu();
    FoodMenuResponse getMenuItemById(Long id);
    FoodOrderResponse getFoodOrderByReservationId(Long reservationId);
    FoodOrderResponse orderFoodForReservation(Long reservationId, FoodOrderRequest request);
    FoodOrderResponse cancelFoodOrder(Long reservationId, Long foodOrderId);

    // Admin
    FoodMenuResponse createMenuItem(FoodMenuRequest request);
    FoodMenuResponse updateMenuItem(Long id, FoodMenuRequest request);
    void deleteMenuItem(Long id);
    FoodMenuResponse updateAvailability(Long id, boolean available);

    // Internal calculation helper for Saga
    FoodOrderResponse createInternalFoodOrder(Long reservationId, String pnr, List<FoodItemSelection> items);
}