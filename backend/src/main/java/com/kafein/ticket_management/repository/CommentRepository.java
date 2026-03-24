package com.kafein.ticket_management.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kafein.ticket_management.model.Comment;
import com.kafein.ticket_management.model.Ticket;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.author WHERE c.ticket = :ticket")
    List<Comment> findAllByTicketIdWithAuthor(@Param("ticket") Ticket ticket);
    
}
