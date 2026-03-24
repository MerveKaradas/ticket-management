package com.kafein.ticket_management.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kafein.ticket_management.model.Ticket;
import com.kafein.ticket_management.model.enums.TicketStatus;

public interface TicketRepository extends JpaRepository<Ticket, UUID>, JpaSpecificationExecutor<Ticket> {
    List<Ticket> findTop5ByOrderByCreatedAtDateDesc();

    @Query("SELECT t.status, COUNT(t) FROM Ticket t GROUP BY t.status")
    List<Object[]> countTicketsByStatusRaw();

    @Query("SELECT t.priority, COUNT(t) FROM Ticket t GROUP BY t.priority")
    List<Object[]> countTicketsByPriorityRaw();

    List<Ticket> findAllByStatus(TicketStatus ticketStatus);

    @Query("SELECT CONCAT(t.assignedTo.name, ' ', t.assignedTo.surname), COUNT(t) FROM Ticket t WHERE t.status != 'DONE' GROUP BY t.assignedTo.name ,t.assignedTo.surname")
    List<Object[]> countTicketsByFullAssigneeName();

    @Query("SELECT t.status, COUNT(t) FROM Ticket t " +
            "WHERE t.updatedDate >= :startOfDay " +
            "GROUP BY t.status")
    List<Object[]> countDailyTrendByStatus(@Param("startOfDay") LocalDateTime startOfDay);

    @Override
    @EntityGraph(attributePaths = { "createdBy", "assignedTo", "updatedBy" })
    List<Ticket> findAll();

    @Override
    @EntityGraph(attributePaths = { "createdBy", "assignedTo" })
    Page<Ticket> findAll(Specification<Ticket> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"assignedTo"})
    List<Ticket> findAllByassignedTo_IdAndStatusNot(UUID userId, TicketStatus done);

    @Override
    @EntityGraph(attributePaths = { "createdBy", "assignedTo", "updatedBy" })
    Optional<Ticket> findById(UUID id);

}
