package com.kafein.ticket_management.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    
    
}
