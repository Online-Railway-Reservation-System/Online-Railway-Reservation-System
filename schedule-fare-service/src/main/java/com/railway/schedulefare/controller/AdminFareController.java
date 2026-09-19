package com.railway.schedulefare.controller;

import com.railway.schedulefare.dto.ApiResponse;
import com.railway.schedulefare.entity.FareRule;
import com.railway.schedulefare.entity.TatkalConfig;
import com.railway.schedulefare.service.FareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/fares")
@Tag(name = "Admin Fare & Tatkal Management", description = "Administrator configuration of fare rules and Tatkal window")
public class AdminFareController {

    private final FareService fareService;

    public AdminFareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping
    @Operation(summary = "Add a fare rule")
    public ResponseEntity<ApiResponse<FareRule>> createFareRule(@RequestBody FareRule rule) {
        FareRule created = fareService.createFareRule(rule);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Fare rule created successfully", created));
    }

    @PostMapping("/tatkal-config")
    @Operation(summary = "Configure Tatkal rules (advance days, opening times, surcharges) for a train")
    public ResponseEntity<ApiResponse<TatkalConfig>> configureTatkal(@RequestBody TatkalConfig config) {
        TatkalConfig saved = fareService.saveTatkalConfig(config);
        return ResponseEntity.ok(ApiResponse.ok("Tatkal configuration saved successfully", saved));
    }
}