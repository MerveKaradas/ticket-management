package com.kafein.ticket_management.controller;

import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.response.ResponseTicketDto;
import com.kafein.ticket_management.model.enums.TicketStatus;
import com.kafein.ticket_management.service.TicketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Dashboard API", description = "Ticket Bazlı Bilgi İşlemleri")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final TicketService ticketService;

    public DashboardController(TicketService ticketService){
        this.ticketService = ticketService;
    }

    @Operation(summary = "Toplam Ticket Sayısı")
    @GetMapping("/totalTicket")
    public ResponseEntity<Long> getTotalTicketCount(){
        return ResponseEntity.ok(ticketService.totalTicketCount());
    }

    @Operation(summary = "Status Bazlı Ticket Sayısı")
    @GetMapping("/totalStatusTicket")
    public ResponseEntity<Map<TicketStatus,Long>> getTotalStatusTicketCount(){
        return ResponseEntity.ok(ticketService.getEachStatusTotalTicketsCount());
    }

    @Operation(summary = "Son Oluşturulan 5 Ticket")
    @GetMapping("/getLast5Ticket")
    public ResponseEntity<List<ResponseTicketDto>> getLast5Ticket(){
        return ResponseEntity.ok(ticketService.getLast5Tickets());
    }

    
    
}
