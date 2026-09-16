package com.railway.reservation.service;

import com.railway.reservation.dto.PassengerResponse;
import com.railway.reservation.dto.TicketResponse;
import com.railway.reservation.entity.Passenger;
import com.railway.reservation.entity.Ticket;
import com.railway.reservation.exception.ResourceNotFoundException;
import com.railway.reservation.entity.Reservation;
import com.railway.reservation.repository.PassengerRepository;
import com.railway.reservation.repository.ReservationRepository;
import com.railway.reservation.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final PassengerRepository passengerRepository;
    private final ReservationRepository reservationRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, PassengerRepository passengerRepository, ReservationRepository reservationRepository) {
        this.ticketRepository = ticketRepository;
        this.passengerRepository = passengerRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getTicketByPnr(String pnr) {
        Ticket ticket = ticketRepository.findByPnr(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with PNR: " + pnr));
        return mapToDto(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        return mapToDto(ticket);
    }

    @Override
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    private TicketResponse mapToDto(Ticket t) {
        List<Passenger> passengers = passengerRepository.findByReservationId(t.getReservationId());
        List<PassengerResponse> passDtos = passengers.stream()
                .map(p -> new PassengerResponse(p.getId(), p.getName(), p.getGender(), p.getAge(), p.getSeatPreference(), p.getSeatNumber(), p.getBerthType(), p.getStatus(), p.getConcessionType()))
                .collect(Collectors.toList());

        TicketResponse res = new TicketResponse();
        res.setId(t.getId());
        res.setReservationId(t.getReservationId());
        res.setPnr(t.getPnr());
        res.setTrainNumber(t.getTrainNumber());
        res.setTrainName(t.getTrainName());
        res.setJourneyDate(t.getJourneyDate());
        res.setSourceStation(t.getSourceStation());
        res.setDestinationStation(t.getDestinationStation());
        res.setDepartureTime(t.getDepartureTime());
        res.setArrivalTime(t.getArrivalTime());
        res.setTotalAmount(t.getTotalAmount());
        res.setStatus(t.getStatus());

        Reservation reservation = reservationRepository.findById(t.getReservationId()).orElse(null);
        if (reservation != null) {
            res.setBaseFare(reservation.getBaseFare() != null ? reservation.getBaseFare() : reservation.getTicketFare());
            res.setConcessionDiscount(reservation.getConcessionDiscount() != null ? reservation.getConcessionDiscount() : 0.0);
            res.setReservationFee(reservation.getReservationFee() != null ? reservation.getReservationFee() : 50.0);
            res.setFoodTotal(reservation.getFoodTotal() != null ? reservation.getFoodTotal() : 0.0);
        } else {
            res.setBaseFare(t.getTotalAmount());
            res.setConcessionDiscount(0.0);
            res.setReservationFee(0.0);
            res.setFoodTotal(0.0);
        }

        res.setPassengers(passDtos);
        res.setBookedAt(t.getCreatedAt());
        return res;
    }
}