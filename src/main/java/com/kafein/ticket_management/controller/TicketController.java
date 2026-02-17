package com.kafein.ticket_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.request.RequestCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.service.TicketService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/createTicket")
    public ResponseEntity<ResponseCreateTicketDto> createTicket(@RequestBody @Valid RequestCreateTicketDto requestCreateTicketDto){
        return ResponseEntity.ok(ticketService.createTicket(requestCreateTicketDto));
    }
    
}
