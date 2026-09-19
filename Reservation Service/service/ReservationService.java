package com.railway.reservation.service;

import com.railway.reservation.dto.ReservationRequest;
import com.railway.reservation.dto.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReservationService {
    ReservationResponse bookReservation(Long customerId, String email, ReservationRequest request, String idempotencyKey);
    ReservationResponse getReservationById(Long id);
    ReservationResponse getReservationByPnr(String pnr);
    List<ReservationResponse> getReservationsByCustomerId(Long customerId);
    List<ReservationResponse> getReservationsByCustomer(Long customerId, String email);
    ReservationResponse cancelReservation(Long id, String reason);
    Page<ReservationResponse> getAllReservations(Pageable pageable);
    java.util.Map<String, Object> broadcastTrainDelay(com.railway.reservation.dto.TrainDelayNotificationRequest request);
}
