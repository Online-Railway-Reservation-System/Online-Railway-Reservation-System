package com.railway.schedulefare.controller;

import com.railway.schedulefare.dto.ApiResponse;
import com.railway.schedulefare.dto.ScheduleResponse;
import com.railway.schedulefare.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@Tag(name = "Train Schedules", description = "Train departure/arrival times, running days, and duration")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/train/{trainId}")
    @Operation(summary = "Get train schedules by train id")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedulesByTrainId(@PathVariable Long trainId) {
        return ResponseEntity.ok(ApiResponse.ok(scheduleService.getSchedulesByTrainId(trainId)));
    }
}