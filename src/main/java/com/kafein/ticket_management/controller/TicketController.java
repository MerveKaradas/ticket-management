package com.kafein.ticket_management.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.request.RequestCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseGetAllTicketsDto;
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

    @GetMapping("/getAllTickets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ResponseGetAllTicketsDto>> getAllTickets(){
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @DeleteMapping("/deleteAllTickets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAllTickets(){
        ticketService.deleteAllTickets();
        return ResponseEntity.ok().build();
        
    }

    @DeleteMapping("/deleteTicket/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicketById(@PathVariable UUID id){
        ticketService.deleteTicketById(id);
        return ResponseEntity.ok().build();
        
    }
    

}
