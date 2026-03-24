package com.kafein.ticket_management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kafein.ticket_management.dto.response.ResponseAuditLogDto;
import com.kafein.ticket_management.model.AuditLog;

@Service
public class AdminService {

    private final TokenService tokenService;
    private final AuditLogService auditLogService; 

    public AdminService(TokenService tokenService, AuditLogService auditLogService) {
        this.tokenService = tokenService;
        this.auditLogService = auditLogService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<AuditLog> findAll(String query, Pageable pageable) {
        return auditLogService.findAll(query, pageable);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void revokeAllTokens() {
        tokenService.revokeAllTokens();
    }

    public Double calculateSystemFailRate() {
        return auditLogService.calculateSystemFailRate();
    }

    public List<ResponseAuditLogDto> getRecentSecurityLogs() {
        return auditLogService.getRecentSecurityLogs();
    }

}
