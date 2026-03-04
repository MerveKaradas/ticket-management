package com.kafein.ticket_management.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.request.RequestCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseTicketDto;
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;
import com.kafein.ticket_management.service.TicketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.kafein.ticket_management.dto.request.RequestTicketDto;
import com.kafein.ticket_management.dto.request.TicketStatusUpdateRequestDto;

import jakarta.validation.Valid;

@CrossOrigin(
    origins = "http://localhost:5173", 
    allowedHeaders = "*", 
    methods = {
        RequestMethod.GET, 
        RequestMethod.POST, 
        RequestMethod.PUT, 
        RequestMethod.PATCH, 
        RequestMethod.DELETE, 
        RequestMethod.OPTIONS
    }
)@Tag(name = "Ticket API", description = "Bilet oluşturma, temel CRUD işlemleri ve filtreleme işlemleri")
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(summary = "Yeni Bilet Oluşturma")
    @PostMapping("/createTicket")
    public ResponseEntity<ResponseCreateTicketDto> createTicket(@RequestBody @Valid RequestCreateTicketDto requestCreateTicketDto){
        return ResponseEntity.ok(ticketService.createTicket(requestCreateTicketDto));
    }

    @Operation(summary = "Tüm Biletleri Görüntüleme", 
                description = "'ADMIN' veya 'USER' yetkisine sahip olan kullanıcı tüm biletleri görünteyebilir.")
    @GetMapping("/getAllTickets")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ResponseTicketDto>> getAllTickets(){
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @Operation(summary = "Tüm Biletleri Silme", 
                description = "'ADMIN' yetkisine sahip olan kullanıcı tüm biletleri silebilir.")
    @DeleteMapping("/deleteAllTickets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAllTickets(){
        ticketService.deleteAllTickets();
        return ResponseEntity.ok().build();
        
    }

    @Operation(summary = "Bilet Silme", 
                description = "'ADMIN' yetkisine sahip olan kullanıcı bileti silebilir.")
    @DeleteMapping("/deleteTicket/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicketById(@PathVariable UUID id){
        ticketService.deleteTicketById(id);
        return ResponseEntity.ok().build();
        
    }
    
    @Operation(summary = "Biletleri Filtreleyerek Listeleme", 
                description = "Filtreleme seçeneklerine uygun olan biletler listelenir.")
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

    @Operation(summary = "Bilet Durumunu Güncelleme (Statü Geçişi)", 
                description = "Biletin durumunu OPEN -> IN_PROGRESS -> DONE sırasıyla günceller. Sadece bilete atanan kullanıcı bu işlemi yapabilir. DONE durumundaki biletler tekrar OPEN yapılamaz.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseTicketDto> updateTicketStatus(@PathVariable UUID id, @RequestBody TicketStatusUpdateRequestDto status) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, status));
    }


    @Operation(summary = "Bilet İçeriğini Değiştirme", 
                description = "Sadece bilete atanan kullanıcı bilet içeriğini güncelleyebilir.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseTicketDto> updateTicket(@PathVariable("id") UUID ticketId, @RequestBody @Valid RequestTicketDto requestTicketDto){
        return ResponseEntity.ok(ticketService.updateTicket(ticketId,requestTicketDto));
    }
}
