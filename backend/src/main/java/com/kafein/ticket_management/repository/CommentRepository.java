package com.kafein.ticket_management.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kafein.ticket_management.model.Comment;
import com.kafein.ticket_management.model.Ticket;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findAllByTicketOrderByCreatedAtDesc(Ticket ticket);
    
}
