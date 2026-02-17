package com.kafein.ticket_management.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kafein.ticket_management.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, UUID>{
    
}
