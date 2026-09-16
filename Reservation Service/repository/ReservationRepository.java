package com.railway.reservation.repository;

import com.railway.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByPnr(String pnr);
    List<Reservation> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Reservation> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
    List<Reservation> findByCustomerIdOrCustomerEmailOrderByCreatedAtDesc(Long customerId, String customerEmail);
    List<Reservation> findByStatus(String status);
    List<Reservation> findByTrainNumberIgnoreCaseAndJourneyDateAndStatusIn(String trainNumber, java.time.LocalDate journeyDate, List<String> statuses);
}