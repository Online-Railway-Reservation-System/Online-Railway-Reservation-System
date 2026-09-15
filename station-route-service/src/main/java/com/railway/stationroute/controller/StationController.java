package com.railway.stationroute.controller;

import com.railway.stationroute.dto.ApiResponse;
import com.railway.stationroute.dto.StationResponse;
import com.railway.stationroute.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stations")
@Tag(name = "Stations", description = "View station catalog and station details")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    @Operation(summary = "Get all active stations")
    public ResponseEntity<ApiResponse<List<StationResponse>>> getAllStations() {
        return ResponseEntity.ok(ApiResponse.ok(stationService.getAllStations()));
    }

    @GetMapping("/{stationId}")
    @Operation(summary = "Get station by id")
    public ResponseEntity<ApiResponse<StationResponse>> getStationById(@PathVariable Long stationId) {
        return ResponseEntity.ok(ApiResponse.ok(stationService.getStationById(stationId)));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get station by code")
    public ResponseEntity<ApiResponse<StationResponse>> getStationByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(stationService.getStationByCode(code)));
    }
}