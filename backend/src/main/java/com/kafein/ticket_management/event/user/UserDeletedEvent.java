package com.kafein.ticket_management.event.user;

import java.util.UUID;

import lombok.Getter;

@Getter
public class UserDeletedEvent {

    private final UUID userId;

    private final String email;

    private final String fullName;

    public UserDeletedEvent(UUID userId, String email, String fullName) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
    }

}