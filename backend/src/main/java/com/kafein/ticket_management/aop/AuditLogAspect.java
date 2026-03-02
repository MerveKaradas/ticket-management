package com.kafein.ticket_management.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.kafein.ticket_management.model.AuditLog;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.model.enums.AuditLogStatus;
import com.kafein.ticket_management.repository.AuditLogRepository;

@Aspect
@Component
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;

    public AuditLogAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // İşlem BAŞARILI
    @AfterReturning(pointcut = "@annotation(audit)", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Audit audit, Object result) {
        saveLog(joinPoint, audit, AuditLogStatus.SUCCESS, null);
    }

    // İşlem HATALI
    @AfterThrowing(pointcut = "@annotation(audit)", throwing = "ex")
    public void logFailure(JoinPoint joinPoint, Audit audit, Exception ex) {
        saveLog(joinPoint, audit, AuditLogStatus.FAILED, ex.getMessage());
    }

    // TODO : ASYNC YAP SONRA
    private void saveLog(JoinPoint joinPoint, Audit audit, AuditLogStatus status, String error) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = "System / Anonymous";

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
           
            if (auth.getPrincipal() instanceof User user) {
                username = user.getEmail(); 
            } 
        }
        
        String message = String.format("Metot: %s, %s tarafından %s olarak çalıştırıldı!",
                joinPoint.getSignature().getName(),
                username,
                status);

        AuditLog log = AuditLog.builder()
                .operation(audit.action())
                .performedBy(username)
                .status(status)
                .errorMessage(error)
                .details(message)
                .build();

        auditLogRepository.save(log);
    }
}
