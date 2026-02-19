package com.kafein.ticket_management.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;

public record ResponseCreateTicketDto(
    UUID id,
    String title,
    String description,
    TicketStatus status,
    TicketPriority priority,
    ResponseUserDto createdBy,
    ResponseUserDto assignedTo,
    LocalDateTime createdAtDate,
    LocalDateTime updatedDate) {

}
