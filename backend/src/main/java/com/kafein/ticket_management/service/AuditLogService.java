package com.kafein.ticket_management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kafein.ticket_management.dto.response.ResponseAuditLogDto;
import com.kafein.ticket_management.model.AuditLog;
import com.kafein.ticket_management.model.enums.AuditLogStatus;
import com.kafein.ticket_management.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    // Propagation.REQUIRES_NEW : Ana işlem hata alsa bile logun kaydedilmesini sağlar
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createLog(String operation, String performedBy, AuditLogStatus status, String details, String errorMessage) {
        AuditLog log = AuditLog.builder()
                .operation(operation)
                .performedBy(performedBy)
                .status(status)
                .details(details)
                .errorMessage(errorMessage)
                .build();
        
        auditLogRepository.save(log);
    }

    public Page<AuditLog> findAll(String query, Pageable pageable) {
        String searchQuery = (query != null && !query.trim().isEmpty()) ? query : null;

        return auditLogRepository.searchLogs(searchQuery, pageable);
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
