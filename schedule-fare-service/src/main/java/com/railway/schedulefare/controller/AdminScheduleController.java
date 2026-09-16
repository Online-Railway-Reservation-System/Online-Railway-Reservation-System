package com.railway.schedulefare.controller;

import com.railway.schedulefare.dto.ApiResponse;
import com.railway.schedulefare.dto.ScheduleRequest;
import com.railway.schedulefare.dto.ScheduleResponse;
import com.railway.schedulefare.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/schedules")
@Tag(name = "Admin Schedule Management", description = "Administrator CRUD for train schedules")
public class AdminScheduleController {

    private final ScheduleService scheduleService;

    public AdminScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    @Operation(summary = "Create a train schedule")
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        ScheduleResponse created = scheduleService.createSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Schedule created successfully", created));
    }

    @PutMapping("/{scheduleId}")
    @Operation(summary = "Update a train schedule")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(@PathVariable Long scheduleId,
                                                                         @Valid @RequestBody ScheduleRequest request) {
        ScheduleResponse updated = scheduleService.updateSchedule(scheduleId, request);
        return ResponseEntity.ok(ApiResponse.ok("Schedule updated successfully", updated));
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "Deactivate a train schedule")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(ApiResponse.message("Schedule deactivated successfully"));
    }
}