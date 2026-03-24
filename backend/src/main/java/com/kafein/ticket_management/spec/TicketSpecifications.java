package com.kafein.ticket_management.spec;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.kafein.ticket_management.model.Ticket;
import com.kafein.ticket_management.model.Ticket_;
import com.kafein.ticket_management.model.User_;
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;

public class TicketSpecifications {

    public static Specification<Ticket> hasTitle(String title) {
        return (root, query, cb) -> (title == null || title.isBlank()) ? null
                : cb.like(cb.lower(root.get(Ticket_.TITLE)), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Ticket> hasStatus(TicketStatus status) {
        return (root, query, cb) -> (status == null) ? null : cb.equal(root.get(Ticket_.STATUS), status);
    }

    public static Specification<Ticket> hasPriority(TicketPriority priority) {
        return (root, query, cb) -> (priority == null) ? null : cb.equal(root.get(Ticket_.PRIORITY), priority);
    }

    public static Specification<Ticket> hasAssignedTo(UUID userId) {
        return (root, query, cb) -> (userId == null) ? null : cb.equal(root.get(Ticket_.ASSIGNED_TO).get(User_.ID), userId);
    }

}
