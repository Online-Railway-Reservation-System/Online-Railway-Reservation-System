package com.railway.train.controller;

import com.railway.train.dto.ApiResponse;
import com.railway.train.dto.TrainResponse;
import com.railway.train.service.TrainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trains")
@Tag(name = "Train Service", description = "Public operations for searching and viewing train details")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping
    @Operation(summary = "Get all active trains")
    public ResponseEntity<ApiResponse<List<TrainResponse>>> getAllTrains() {
        return ResponseEntity.ok(ApiResponse.ok(trainService.getAllActiveTrains()));
    }

    @GetMapping("/{trainId}")
    @Operation(summary = "Get train details by id")
    public ResponseEntity<ApiResponse<TrainResponse>> getTrainById(@PathVariable Long trainId) {
        return ResponseEntity.ok(ApiResponse.ok(trainService.getTrainById(trainId)));
    }

    @GetMapping("/number/{trainNumber}")
    @Operation(summary = "Get train details by train number")
    public ResponseEntity<ApiResponse<TrainResponse>> getTrainByNumber(@PathVariable String trainNumber) {
        return ResponseEntity.ok(ApiResponse.ok(trainService.getTrainByNumber(trainNumber)));
    }
}