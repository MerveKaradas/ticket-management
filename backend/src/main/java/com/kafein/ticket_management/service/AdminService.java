package com.kafein.ticket_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kafein.ticket_management.model.AuditLog;
import com.kafein.ticket_management.repository.AuditLogRepository;


@Service
public class AdminService {

    private final TokenService tokenService;
    private final AuditLogRepository auditLogRepository;

    public AdminService(TokenService tokenService, AuditLogRepository auditLogRepository) {
        this.tokenService = tokenService;
        this.auditLogRepository = auditLogRepository;
    }

    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Transactional 
    public void revokeAllTokens() {
        tokenService.revokeAllTokens();
    }

   
    
}
