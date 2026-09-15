package com.railway.reservation.controller;

import com.railway.reservation.dto.ApiResponse;
import com.railway.reservation.dto.ReservationRequest;
import com.railway.reservation.dto.ReservationResponse;
import com.railway.reservation.service.ReservationService;
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
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation and Booking", description = "Ticket reservation Saga orchestration, booking history, and cancellations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @Operation(summary = "Book a new reservation (Orchestrates Saga: concession verify, fare calc, food order, seat hold, payment, confirm)")
    public ResponseEntity<ApiResponse<ReservationResponse>> bookReservation(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody ReservationRequest request) {

        Long customerId = parseUserId(userIdHeader);
        if (customerId == null && request.getCustomerId() != null) {
            customerId = request.getCustomerId();
        }
        if (customerId == null) {
            customerId = 1L;
        }
        String email = emailHeader;
        if ((email == null || email.isBlank()) && request.getCustomerEmail() != null && !request.getCustomerEmail().isBlank()) {
            email = request.getCustomerEmail().trim();
        }
        ReservationResponse response = reservationService.bookReservation(customerId, email, request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation details by ID")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getReservationById(id)));
    }

    @GetMapping("/pnr/{pnr}")
    @Operation(summary = "Get reservation details by 10-digit PNR")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservationByPnr(@PathVariable String pnr) {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getReservationByPnr(pnr)));
    }

    @GetMapping("/my")
    @Operation(summary = "Get current authenticated customer's booking history")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getMyReservations(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader) {
        Long customerId = parseUserId(userIdHeader);
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getReservationsByCustomer(customerId, emailHeader)));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get reservations by customer ID (Admin / Support)")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getReservationsByCustomer(
            @PathVariable Long customerId,
            @RequestParam(required = false) String email) {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getReservationsByCustomer(customerId, email)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an existing reservation (Releases seats, triggers refund, cancels catering)")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancelReservation(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Cancelled by user") String reason) {
        return ResponseEntity.ok(ApiResponse.ok("Reservation cancelled successfully", reservationService.cancelReservation(id, reason)));
    }

    @GetMapping
    @Operation(summary = "Get all reservations paged (Admin)")
    public ResponseEntity<ApiResponse<Page<ReservationResponse>>> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getAllReservations(pageable)));
    }

    @PostMapping({"/admin/notify-delay", "/notify-delay"})
    @Operation(summary = "Broadcast train delay alert to all passengers booked for a train on a specific journey date (Admin OCC)")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> broadcastTrainDelay(
            @Valid @RequestBody com.railway.reservation.dto.TrainDelayNotificationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Train delay notifications processed", reservationService.broadcastTrainDelay(request)));
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
