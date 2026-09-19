package com.railway.schedulefare.controller;

import com.railway.schedulefare.dto.ApiResponse;
import com.railway.schedulefare.dto.FareCalculateRequest;
import com.railway.schedulefare.dto.FareCalculateResponse;
import com.railway.schedulefare.entity.FareRule;
import com.railway.schedulefare.entity.TatkalConfig;
import com.railway.schedulefare.service.FareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fares")
@Tag(name = "Fares and Tatkal", description = "Authoritative server-side fare calculation and Tatkal rules")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @GetMapping("/train/{trainId}")
    @Operation(summary = "Get configured fare rules for a train")
    public ResponseEntity<ApiResponse<List<FareRule>>> getFaresByTrainId(@PathVariable Long trainId) {
        return ResponseEntity.ok(ApiResponse.ok(fareService.getFareRulesByTrainId(trainId)));
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate final ticket fare server-side (supports class, Tatkal, and verified concessions)")
    public ResponseEntity<ApiResponse<FareCalculateResponse>> calculateFare(@Valid @RequestBody FareCalculateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(fareService.calculateFare(request)));
    }

    @GetMapping("/tatkal-config/{trainId}")
    @Operation(summary = "Get Tatkal booking rules and opening times for a train")
    public ResponseEntity<ApiResponse<TatkalConfig>> getTatkalConfig(@PathVariable Long trainId) {
        return ResponseEntity.ok(ApiResponse.ok(fareService.getTatkalConfig(trainId)));
    }
}