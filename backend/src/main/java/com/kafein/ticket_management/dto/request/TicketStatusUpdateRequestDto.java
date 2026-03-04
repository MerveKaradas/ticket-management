package com.kafein.ticket_management.dto.request;

import com.kafein.ticket_management.model.enums.TicketStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TicketStatusUpdateRequestDto(
    @Schema(implementation = TicketStatus.class, example = "IN_PROGRESS")
    @NotNull(message = "Status durumu belirtilmelidir")
    TicketStatus status
) {
    
}
