package com.railway.customer.controller;

import com.railway.customer.dto.AdminReplyRequest;
import com.railway.customer.dto.ApiResponse;
import com.railway.customer.dto.CustomerQueryRequest;
import com.railway.customer.dto.CustomerQueryResponse;
import com.railway.customer.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Support", description = "Helpdesk, inquiries, and customer care ticket management")
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PostMapping("/support/queries")
    @Operation(summary = "Submit a customer support query or complaint")
    public ResponseEntity<ApiResponse<CustomerQueryResponse>> submitQuery(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader,
            @Valid @RequestBody CustomerQueryRequest request) {
        Long userId = parseUserId(userIdHeader);
        CustomerQueryResponse response = supportService.submitQuery(userId, emailHeader, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Query submitted successfully", response));
    }

    @GetMapping("/support/my-queries")
    @Operation(summary = "Get list of support queries submitted by authenticated customer")
    public ResponseEntity<ApiResponse<List<CustomerQueryResponse>>> getMyQueries(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader) {
        Long userId = parseUserId(userIdHeader);
        return ResponseEntity.ok(ApiResponse.ok(supportService.getMyQueries(userId, emailHeader)));
    }

    @GetMapping("/support/queries/{id}")
    @Operation(summary = "Get query ticket details by ID")
    public ResponseEntity<ApiResponse<CustomerQueryResponse>> getQueryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(supportService.getQueryById(id)));
    }

    @GetMapping("/admin/support/queries")
    @Operation(summary = "Get all customer support queries with optional status filter (Admin)")
    public ResponseEntity<ApiResponse<List<CustomerQueryResponse>>> getAllQueries(
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        return ResponseEntity.ok(ApiResponse.ok(supportService.getAllQueries(status)));
    }

    @PostMapping("/admin/support/queries/{id}/reply")
    @Operation(summary = "Send admin reply to customer support query (Admin)")
    public ResponseEntity<ApiResponse<CustomerQueryResponse>> replyToQuery(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Email", required = false) String adminEmail,
            @Valid @RequestBody AdminReplyRequest request) {
        CustomerQueryResponse response = supportService.replyToQuery(id, adminEmail, request);
        return ResponseEntity.ok(ApiResponse.ok("Reply submitted successfully", response));
    }

    private Long parseUserId(String userIdStr) {
        if (userIdStr != null && !userIdStr.isBlank()) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
