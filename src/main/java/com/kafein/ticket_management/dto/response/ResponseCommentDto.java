package com.kafein.ticket_management.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseCommentDto(
        UUID id,
        String content,
        UUID authorId,
        UUID ticketId,
        LocalDateTime createdAt) {

}
