package com.kafein.ticket_management.dto.response;

import java.time.LocalDateTime;

import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;

import lombok.Builder;

@Builder
public record ResponseGetAllTicketsDto(
    String title,
    String description,
    TicketStatus status,
    TicketPriority priority,
    ResponseUserDto createdBy,
    ResponseUserDto updatedBy,
    ResponseUserDto assignedTo,
    LocalDateTime createdAtDate,
    LocalDateTime updatedDate
) {}
