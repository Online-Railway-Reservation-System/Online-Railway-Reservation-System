package com.railway.inventoryquota.controller;

import com.railway.inventoryquota.dto.ApiResponse;
import com.railway.inventoryquota.entity.Coach;
import com.railway.inventoryquota.entity.TrainQuota;
import com.railway.inventoryquota.repository.CoachRepository;
import com.railway.inventoryquota.service.QuotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Inventory Management", description = "Administrator configuration of coaches and quota allocations")
public class AdminInventoryController {

    private final CoachRepository coachRepository;
    private final QuotaService quotaService;

    public AdminInventoryController(CoachRepository coachRepository, QuotaService quotaService) {
        this.coachRepository = coachRepository;
        this.quotaService = quotaService;
    }

    @PostMapping("/coaches")
    @Operation(summary = "Add a coach to a train")
    public ResponseEntity<ApiResponse<Coach>> addCoach(@RequestBody Coach coach) {
        Coach saved = coachRepository.save(coach);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Coach created successfully", saved));
    }

    @PostMapping("/quotas")
    @Operation(summary = "Configure a quota for a train")
    public ResponseEntity<ApiResponse<TrainQuota>> addQuota(@RequestBody TrainQuota quota) {
        TrainQuota saved = quotaService.saveQuota(quota);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Quota configured successfully", saved));
    }
}