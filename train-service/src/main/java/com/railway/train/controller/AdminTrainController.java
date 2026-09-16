package com.railway.train.controller;

import com.railway.train.dto.ApiResponse;
import com.railway.train.dto.TrainRequest;
import com.railway.train.dto.TrainResponse;
import com.railway.train.service.TrainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/trains")
@Tag(name = "Admin Train Management", description = "Administrator CRUD for train master catalog")
public class AdminTrainController {

    private final TrainService trainService;

    public AdminTrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @PostMapping
    @Operation(summary = "Create a new train")
    public ResponseEntity<ApiResponse<TrainResponse>> createTrain(@Valid @RequestBody TrainRequest request) {
        TrainResponse created = trainService.createTrain(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Train created successfully", created));
    }

    @PutMapping("/{trainId}")
    @Operation(summary = "Update an existing train")
    public ResponseEntity<ApiResponse<TrainResponse>> updateTrain(@PathVariable Long trainId,
                                                                 @Valid @RequestBody TrainRequest request) {
        TrainResponse updated = trainService.updateTrain(trainId, request);
        return ResponseEntity.ok(ApiResponse.ok("Train updated successfully", updated));
    }

    @DeleteMapping("/{trainId}")
    @Operation(summary = "Soft delete / deactivate a train")
    public ResponseEntity<ApiResponse<Void>> deleteTrain(@PathVariable Long trainId) {
        trainService.deleteTrain(trainId);
        return ResponseEntity.ok(ApiResponse.message("Train deactivated successfully"));
    }
}