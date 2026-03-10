package com.kafein.ticket_management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kafein.ticket_management.dto.response.ResponseAuditLogDto;
import com.kafein.ticket_management.model.AuditLog;
import com.kafein.ticket_management.model.enums.AuditLogStatus;
import com.kafein.ticket_management.repository.AuditLogRepository;

@Service
public class AdminService {

    private final TokenService tokenService;
    private final AuditLogRepository auditLogRepository;

    public AdminService(TokenService tokenService, AuditLogRepository auditLogRepository) {
        this.tokenService = tokenService;
        this.auditLogRepository = auditLogRepository;
    }

    public Page<AuditLog> findAll(String query, Pageable pageable) {
        String searchQuery = (query != null && !query.trim().isEmpty()) ? query : null;

        return auditLogRepository.searchLogs(searchQuery, pageable);
    }

    @Transactional
    public void revokeAllTokens() {
        tokenService.revokeAllTokens();
    }

    public Double calculateSystemFailRate() {
        long totalLogs = auditLogRepository.count();

        if (totalLogs == 0) {
            return 0.0;
        }

        long failedLogs = auditLogRepository.countByStatus(AuditLogStatus.FAILED);

        double rate = ((double) failedLogs / totalLogs) * 100;

        return Math.round(rate * 100.0) / 100.0;
    }

    public List<ResponseAuditLogDto> getRecentSecurityLogs() {
        List<AuditLog> logs = auditLogRepository.findTop5ByOrderByCreatedAtDateDesc();

        return logs.stream()
                .map(log -> ResponseAuditLogDto.builder()
                        .id(log.getId())
                        .operation(log.getOperation())
                        .details(log.getDetails())
                        .errorMessage(log.getErrorMessage())
                        .performedBy(log.getPerformedBy())
                        .createdAtDate(log.getCreatedAtDate())
                        .status(log.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

}
