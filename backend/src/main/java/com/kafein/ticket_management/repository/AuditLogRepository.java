package com.kafein.ticket_management.repository;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kafein.ticket_management.model.AuditLog;
import com.kafein.ticket_management.model.enums.AuditLogStatus;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    long countByStatus(AuditLogStatus status);

    List<AuditLog> findTop5ByOrderByCreatedAtDateDesc();

    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:query IS NULL OR " +
            "LOWER(CAST(a.operation AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " +
            "LOWER(CAST(a.performedBy AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " +
            "LOWER(CAST(a.status AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " +
            "LOWER(CAST(a.details AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " +
            "LOWER(CAST(a.errorMessage AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')))")
    Page<AuditLog> searchLogs(@Param("query") String query, Pageable pageable);


    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:query IS NULL OR " +
            "LOWER(CAST(a.operation AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " +
            "LOWER(CAST(a.performedBy AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " +
            "LOWER(CAST(a.status AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " +
            "LOWER(CAST(a.details AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " +
            "LOWER(CAST(a.errorMessage AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')))")
    Stream<AuditLog> streamAllByQuery(@Param("query") String query);

}
