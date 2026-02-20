package com.kafein.ticket_management.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.request.RequestCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseTicketDto;
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;
import com.kafein.ticket_management.service.TicketService;
import com.kafein.ticket_management.dto.request.RequestTicketDto;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/tickets")
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
    public ResponseEntity<List<ResponseTicketDto>> getAllTickets(){
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
    
    @GetMapping("/filter") // TODO : page ve size değerlerini kontrol et
    public ResponseEntity<Page<ResponseTicketDto>> filterTickets(
        @RequestParam(required = false) TicketStatus status,
        @RequestParam(required = false) TicketPriority priority,
        @RequestParam(required = false) UUID assignedToId ,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ticketService.filterTickets(status, priority, assignedToId, page, size));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseTicketDto> updateTicketStatus(@PathVariable UUID id, @RequestParam TicketStatus status) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, status));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseTicketDto> updateTicket(@PathVariable("id") UUID ticketId, @RequestBody @Valid RequestTicketDto requestTicketDto){
        return ResponseEntity.ok(ticketService.updateTicket(ticketId,requestTicketDto));
    }
}
