package com.kafein.ticket_management.util;

import java.util.UUID;

import com.kafein.ticket_management.dto.request.RequestCreateTicketDto;
import com.kafein.ticket_management.model.Ticket;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.model.enums.Role;
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;

public class TestDataFactory {
    
    public static Ticket createTestTicket(User user, String title, String description, TicketPriority priority, TicketStatus status) {
        return Ticket.builder()
                .title(title != null ? title : "Kod Optimizasyonu")
                .description(description != null ? description : "N+1 problemlerini gözden geçir!")
                .priority(priority != null ? priority : TicketPriority.LOW)
                .status(status != null ? status : TicketStatus.OPEN)
                .assignedTo(user)
                .build();
    }

    public static Ticket createTestTicket(User user, RequestCreateTicketDto requestDto) {
        return createTestTicket(user, requestDto.title(), requestDto.description(), requestDto.priority(), null);
    }

    public static Ticket createTestTicket(User user, TicketStatus status) {
        return createTestTicket(user, null, null, null, status);
    }

    public static Ticket createTestTicket(User user) {
        return createTestTicket(user, null, null, null, null);
    }

    public static User createTestUser() {
        return createTestUser(null, null, null);
    }

    public static User createTestUser(UUID id) {
        return createTestUser(id, null, null);
    }

    public static User createTestUser(Role role) {
        return createTestUser(null, null, role);
    }

    public static User createTestUser(UUID id, String email, Role role) {
        return User.builder()
                .id(id != null ? id : UUID.randomUUID())
                .name("Kafein")
                .surname("Solutions")
                .email(email != null ? email : "defaultkafein@gmail.com")
                .password("HashedPassword123!")
                .role(role != null ? role : Role.USER)
                .build();
    }

    
    public static User createSystemPoolUser(){
          return User.builder()
                .id(UUID.randomUUID())
                .name("System")
                .surname("Pool")
                .role(Role.SYSTEM)
                .email("systempool@kafein.com")
                .password("HashedPassword123!")
                .build();
    }
    
}
