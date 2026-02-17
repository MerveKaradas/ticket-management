package com.kafein.ticket_management.dto.response;

import com.kafein.ticket_management.model.enums.Role;

import lombok.Builder;


@Builder
public record ResponseUserDto(
    String name,
    String surname,
    String email,
    Role role
) {
    
}
