package com.kafein.ticket_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kafein.ticket_management.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long>{
    
}
