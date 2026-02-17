package com.kafein.ticket_management.service;

import org.springframework.stereotype.Service;

import com.kafein.ticket_management.dto.request.RequestCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.mapper.TicketMapper;
import com.kafein.ticket_management.model.Ticket;
import com.kafein.ticket_management.model.User;
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
                            .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi : " + requestCreateTicketDto.getAssignedToId()));

        Ticket ticket = Ticket.builder()
                .title(requestCreateTicketDto.getTitle())
                .description(requestCreateTicketDto.getDescription())
                .priority(requestCreateTicketDto.getPriority())
                .assignedTo(user)
                .build();
        
        ticketRepository.save(ticket);

        return ticketMapper.toDto(ticket);

    }

}
