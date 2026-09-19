package com.railway.search.controller;

import com.railway.search.dto.ApiResponse;
import com.railway.search.dto.TrainSearchResult;
import com.railway.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/trains")
    public ResponseEntity<ApiResponse<List<TrainSearchResult>>> searchTrains(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate,
            @RequestParam(defaultValue = "SL") String classType,
            @RequestParam(defaultValue = "GENERAL") String quota) {

        List<TrainSearchResult> results = searchService.searchTrains(source, destination, journeyDate, classType, quota);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }
}