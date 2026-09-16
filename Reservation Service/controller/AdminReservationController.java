package com.railway.reservation.controller;

import com.railway.reservation.dto.ApiResponse;
import com.railway.reservation.dto.ReservationResponse;
import com.railway.reservation.dto.TrainDelayNotificationRequest;
import com.railway.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/reservations")
@Tag(name = "Admin Reservations", description = "Admin reservation management and delay broadcasts")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/notify-delay")
    @Operation(summary = "Broadcast train delay alert to passengers on specific train & journey date (Admin OCC)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> broadcastTrainDelay(
            @Valid @RequestBody TrainDelayNotificationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Train delay notifications processed", reservationService.broadcastTrainDelay(request)));
    }

    @GetMapping
    @Operation(summary = "Get all reservations paged (Admin)")
    public ResponseEntity<ApiResponse<Page<ReservationResponse>>> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getAllReservations(pageable)));
    }
}