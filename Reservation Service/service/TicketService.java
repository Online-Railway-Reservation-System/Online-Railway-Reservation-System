package com.railway.reservation.service;

import com.railway.reservation.dto.TicketResponse;
import com.railway.reservation.entity.Ticket;

public interface TicketService {
    TicketResponse getTicketByPnr(String pnr);
    TicketResponse getTicketById(Long id);
    Ticket createTicket(Ticket ticket);
}