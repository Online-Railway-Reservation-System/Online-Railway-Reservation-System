package com.railway.notification.controller;

import com.railway.notification.dto.ApiResponse;
import com.railway.notification.dto.NotificationRequest;
import com.railway.notification.dto.NotificationResponse;
import com.railway.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Email and SMS notifications, delivery logs, and customer communication history")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    @Operation(summary = "Send custom simulated Email/SMS notification")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Notification sent successfully", notificationService.sendNotification(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification log by ID")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotificationById(id)));
    }

    @GetMapping("/pnr/{pnr}")
    @Operation(summary = "Get all notifications sent for a specific PNR")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByPnr(@PathVariable String pnr) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotificationsByPnr(pnr)));
    }

    @GetMapping("/my")
    @Operation(summary = "Get notifications for currently authenticated customer")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        Long customerId = parseUserId(userIdHeader);
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotificationsByCustomerId(customerId)));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get notifications for a customer ID (Admin / Support)")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotificationsByCustomerId(customerId)));
    }

    @GetMapping
    @Operation(summary = "Get all notification logs paged (Admin)")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getAllNotifications(pageable)));
    }

    private Long parseUserId(String userIdStr) {
        if (userIdStr != null && !userIdStr.isBlank()) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException ignored) {}
        }
        return 1L;
    }
}
