package com.railway.stationroute.controller;

import com.railway.stationroute.dto.ApiResponse;
import com.railway.stationroute.dto.RouteStationResponse;
import com.railway.stationroute.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@Tag(name = "Train Routes", description = "View ordered route stops, station sequence, and distances")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/train/{trainId}")
    @Operation(summary = "Get ordered route stops for a train")
    public ResponseEntity<ApiResponse<List<RouteStationResponse>>> getRouteByTrainId(@PathVariable Long trainId) {
        return ResponseEntity.ok(ApiResponse.ok(routeService.getRouteByTrainId(trainId)));
    }
}