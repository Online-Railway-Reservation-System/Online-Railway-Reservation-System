package com.railway.inventoryquota.controller;

import com.railway.inventoryquota.dto.*;
import com.railway.inventoryquota.entity.SeatInventory;
import com.railway.inventoryquota.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Seat Inventory", description = "Seat availability check, atomic hold, confirmation, and release")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/availability")
    @Operation(summary = "Check seat availability for a train, date, class, quota, and optional station leg")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> getAvailability(
            @RequestParam Long trainId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate,
            @RequestParam(defaultValue = "SL") String classType,
            @RequestParam(defaultValue = "GENERAL") String quota,
            @RequestParam(required = false) String fromStationCode,
            @RequestParam(required = false) String toStationCode,
            @RequestParam(required = false, defaultValue = "1") Integer fromStopSeq,
            @RequestParam(required = false, defaultValue = "999") Integer toStopSeq) {
        return ResponseEntity.ok(ApiResponse.ok(
                inventoryService.getAvailability(trainId, journeyDate, classType, quota, fromStationCode, toStationCode, fromStopSeq, toStopSeq)
        ));
    }

    @GetMapping("/seats/{trainId}")
    @Operation(summary = "Get detailed seat layout and statuses for a train, date, and class")
    public ResponseEntity<ApiResponse<List<SeatInventory>>> getSeatsLayout(
            @PathVariable Long trainId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate,
            @RequestParam(defaultValue = "SL") String classType) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getSeatsLayout(trainId, journeyDate, classType)));
    }

    @PostMapping("/hold")
    @Operation(summary = "Atomically hold seats with leg-based non-overlapping reservation")
    public ResponseEntity<ApiResponse<SeatHoldResponse>> holdSeats(@Valid @RequestBody SeatHoldRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Seats held successfully", inventoryService.holdSeats(request)));
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm held seats upon successful payment")
    public ResponseEntity<ApiResponse<SeatConfirmResponse>> confirmSeats(@Valid @RequestBody SeatConfirmRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Seats confirmed successfully", inventoryService.confirmSeats(request)));
    }

    @PostMapping("/release")
    @Operation(summary = "Release held or confirmed seats upon payment failure or ticket cancellation")
    public ResponseEntity<ApiResponse<SeatReleaseResponse>> releaseSeats(@RequestBody SeatReleaseRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Seats released successfully", inventoryService.releaseSeats(request)));
    }
}