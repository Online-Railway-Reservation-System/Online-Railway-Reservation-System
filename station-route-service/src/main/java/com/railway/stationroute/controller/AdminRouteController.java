package com.railway.stationroute.controller;

import com.railway.stationroute.dto.ApiResponse;
import com.railway.stationroute.dto.RouteStationRequest;
import com.railway.stationroute.dto.RouteStationResponse;
import com.railway.stationroute.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/routes")
@Tag(name = "Admin Route Management", description = "Administrator route planning and stop configurations")
public class AdminRouteController {

    private final RouteService routeService;

    public AdminRouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    @Operation(summary = "Add a stop to a train's route")
    public ResponseEntity<ApiResponse<RouteStationResponse>> addRouteStop(@Valid @RequestBody RouteStationRequest request) {
        RouteStationResponse created = routeService.addRouteStop(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Route stop added successfully", created));
    }

    @PutMapping("/{routeId}")
    @Operation(summary = "Update a route stop")
    public ResponseEntity<ApiResponse<RouteStationResponse>> updateRouteStop(@PathVariable Long routeId,
                                                                             @Valid @RequestBody RouteStationRequest request) {
        RouteStationResponse updated = routeService.updateRouteStop(routeId, request);
        return ResponseEntity.ok(ApiResponse.ok("Route stop updated successfully", updated));
    }

    @DeleteMapping("/{routeId}")
    @Operation(summary = "Delete a route stop")
    public ResponseEntity<ApiResponse<Void>> deleteRouteStop(@PathVariable Long routeId) {
        routeService.deleteRouteStop(routeId);
        return ResponseEntity.ok(ApiResponse.message("Route stop deleted successfully"));
    }

    @PostMapping("/train/{trainId}/batch")
    @Operation(summary = "Save complete ordered route stops for a train")
    public ResponseEntity<ApiResponse<java.util.List<RouteStationResponse>>> saveAllRouteStops(
            @PathVariable Long trainId,
            @RequestBody java.util.List<RouteStationRequest> stops) {
        java.util.List<RouteStationResponse> saved = routeService.saveAllRouteStops(trainId, stops);
        return ResponseEntity.ok(ApiResponse.ok("Route stops saved successfully", saved));
    }
}