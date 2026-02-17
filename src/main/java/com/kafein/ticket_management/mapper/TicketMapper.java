package com.kafein.ticket_management.mapper;

import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.model.Ticket;

public class TicketMapper {

    public static ResponseCreateTicketDto toResponseCreateTicketDto(Ticket ticket) {

        return ResponseCreateTicketDto.builder()
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .createdBy(ticket.getCreatedBy())
                .assignedTo(ticket.getAssignedTo())
                .createdAtDate(ticket.getCreatedAtDate())
                .updatedDate(ticket.getUpdatedDate())
                .build();
    }

    
}
