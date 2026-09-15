package com.railway.stationroute.controller;

import com.railway.stationroute.dto.ApiResponse;
import com.railway.stationroute.dto.StationRequest;
import com.railway.stationroute.dto.StationResponse;
import com.railway.stationroute.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/stations")
@Tag(name = "Admin Station Management", description = "Administrator CRUD for railway stations")
public class AdminStationController {

    private final StationService stationService;

    public AdminStationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    @Operation(summary = "Create a new railway station")
    public ResponseEntity<ApiResponse<StationResponse>> createStation(@Valid @RequestBody StationRequest request) {
        StationResponse created = stationService.createStation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Station created successfully", created));
    }

    @PutMapping("/{stationId}")
    @Operation(summary = "Update an existing station")
    public ResponseEntity<ApiResponse<StationResponse>> updateStation(@PathVariable Long stationId,
                                                                     @Valid @RequestBody StationRequest request) {
        StationResponse updated = stationService.updateStation(stationId, request);
        return ResponseEntity.ok(ApiResponse.ok("Station updated successfully", updated));
    }

    @DeleteMapping("/{stationId}")
    @Operation(summary = "Deactivate a station")
    public ResponseEntity<ApiResponse<Void>> deleteStation(@PathVariable Long stationId) {
        stationService.deleteStation(stationId);
        return ResponseEntity.ok(ApiResponse.message("Station deactivated successfully"));
    }
}