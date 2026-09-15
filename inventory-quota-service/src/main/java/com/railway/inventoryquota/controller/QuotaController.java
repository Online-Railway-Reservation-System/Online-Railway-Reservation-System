package com.railway.inventoryquota.controller;

import com.railway.inventoryquota.dto.ApiResponse;
import com.railway.inventoryquota.entity.TrainQuota;
import com.railway.inventoryquota.service.QuotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quotas")
@Tag(name = "Train Quotas", description = "View quota allocations for trains")
public class QuotaController {

    private final QuotaService quotaService;

    public QuotaController(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    @GetMapping("/train/{trainId}")
    @Operation(summary = "Get quota definitions for a train")
    public ResponseEntity<ApiResponse<List<TrainQuota>>> getQuotasByTrainId(@PathVariable Long trainId) {
        return ResponseEntity.ok(ApiResponse.ok(quotaService.getQuotasByTrainId(trainId)));
    }
}