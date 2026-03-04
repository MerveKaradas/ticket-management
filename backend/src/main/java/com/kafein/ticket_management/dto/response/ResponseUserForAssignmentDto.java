package com.kafein.ticket_management.dto.response;

import java.util.UUID;

public record ResponseUserForAssignmentDto(
    UUID id,
    String name,
    String surname
) {
    
}
