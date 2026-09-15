package com.railway.reservation.controller;

import com.railway.reservation.dto.ApiResponse;
import com.railway.reservation.dto.TicketResponse;
import com.railway.reservation.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "Tickets", description = "Ticket retrieval, boarding verification, and PNR status")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/pnr/{pnr}")
    @Operation(summary = "Get ticket details by PNR number")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketByPnr(@PathVariable String pnr) {
        return ResponseEntity.ok(ApiResponse.ok(ticketService.getTicketByPnr(pnr)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket details by Ticket ID")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(ticketService.getTicketById(id)));
    }
}
