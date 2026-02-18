package com.kafein.ticket_management.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kafein.ticket_management.dto.request.RequestCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseTicketDto;
import com.kafein.ticket_management.mapper.TicketMapper;
import com.kafein.ticket_management.model.Ticket;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;
import com.kafein.ticket_management.repository.TicketRepository;

import jakarta.transaction.Transactional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final UserService userService;

    public TicketService(TicketRepository ticketRepository, UserService userService, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.userService = userService;
    }

    @Transactional
    public ResponseCreateTicketDto createTicket(RequestCreateTicketDto requestCreateTicketDto) {

        User user = userService.getUserById(requestCreateTicketDto.getAssignedToId())
                .orElseThrow(() -> new RuntimeException(
                        "Kullanici bulunamadi : " + requestCreateTicketDto.getAssignedToId()));

        Ticket ticket = Ticket.builder()
                .title(requestCreateTicketDto.getTitle())
                .description(requestCreateTicketDto.getDescription())
                .priority(requestCreateTicketDto.getPriority())
                .assignedTo(user)
                .build();

        ticketRepository.save(ticket);

        return ticketMapper.toDto(ticket);

    }

    public List<ResponseTicketDto> getAllTickets() {

        return ticketRepository.findAll()
                .stream()
                .map((ticket) -> ticketMapper.toGetAllTicketsDto(ticket))
                .toList();
    }

    @Transactional
    public void deleteAllTickets() {
        ticketRepository.deleteAll();
    }

    @Transactional
    public void deleteTicketById(UUID id) {
        ticketRepository.deleteById(id);
    }

    
    public Page<ResponseTicketDto> filterTickets(TicketStatus status, TicketPriority priority,
            UUID assignedToId, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Dinamik sorgu oluşturma
        Specification<Ticket> spec = Specification.where((root, query, cb) -> cb.conjunction());
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (priority != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
        }
        if (assignedToId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("assignedTo").get("id"), assignedToId));
        }
        return ticketRepository.findAll(spec, pageable)
                .map(ticketMapper::toGetAllTicketsDto);
    }

}
