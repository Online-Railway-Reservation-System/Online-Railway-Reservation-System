package com.railway.reservation.repository;

import com.railway.reservation.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByPnr(String pnr);
    List<Ticket> findByReservationId(Long reservationId);
}