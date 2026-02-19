package com.kafein.ticket_management.dto.response;

import java.util.UUID;

import com.kafein.ticket_management.model.enums.Role;

public record ResponseUserDto(
    UUID id,
    String name,
    String surname,
    String email,
    Role role
) {
    
}
