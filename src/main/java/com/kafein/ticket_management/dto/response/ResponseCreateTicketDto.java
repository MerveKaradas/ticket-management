package com.kafein.ticket_management.dto.response;

import java.time.LocalDateTime;
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;

import lombok.Builder;

@Builder
public record ResponseCreateTicketDto(
    String title,
    String description,
    TicketStatus status,
    TicketPriority priority,
    ResponseUserDto createdBy,
    ResponseUserDto assignedTo,
    LocalDateTime createdAtDate,
    LocalDateTime updatedDate) {

}
