package com.kafein.ticket_management.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.kafein.ticket_management.dto.request.RequestCreateUserDto;
import com.kafein.ticket_management.dto.request.RequestLoginDto;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.model.enums.AuditLogStatus;
import com.kafein.ticket_management.service.AuditLogService;

@Aspect
@Component
public class AuditLogAspect {

    private final AuditLogService auditLogService;

    public AuditLogAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "System / Anonymous";
        }

        if (auth.getPrincipal() instanceof User user) {
            return user.getEmail();
        }

        return auth.getName();
    }

    // Başarılı İşlemler
    @AfterReturning(pointcut = "@annotation(audit)", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Audit audit, Object result) {
        // Metodun parametrelerinden e-postayı bulma
        String targetEmail = extractEmailFromArgs(joinPoint.getArgs());

        // Eğer parametrelerde email yoksa SecurityContext'e bakıyoruz
        String username = (targetEmail != null) ? targetEmail : getCurrentUsername();
        String message = getMessage(audit.action(), username, true);

        auditLogService.createLog(audit.action(), username, AuditLogStatus.SUCCESS, message, null);
    }

    // Hatalı İşlemler
    @AfterThrowing(pointcut = "@annotation(audit)", throwing = "ex")
    public void logFailure(JoinPoint joinPoint, Audit audit, Exception ex) {
        
        // Metodun parametrelerinden e-postayı bulma
        String targetEmail = extractEmailFromArgs(joinPoint.getArgs());

        // Eğer parametrelerde email yoksa SecurityContext'e bakıyoruz
        String username = (targetEmail != null) ? targetEmail : getCurrentUsername();

        String message = getMessage(audit.action(), username, false);

        auditLogService.createLog(audit.action(), username, AuditLogStatus.FAILED, message, ex.getMessage());
    }

    private String extractEmailFromArgs(Object[] args) {
        if (args == null || args.length == 0)
            return null;

        for (Object arg : args) {
            // Register durumu
            if (arg instanceof RequestCreateUserDto dto) {
                return dto.email();
            }

            // Login durumu
            if (arg instanceof RequestLoginDto loginDto) {
                return loginDto.email();
            }
        }
        return null;
    }

    private String getMessage(String action, String user, boolean isSuccess) {
        if (isSuccess) {
            return switch (action) {
                case "USER_CREATED" -> "Yeni kullanıcı kaydı başarıyla oluşturuldu.";
                case "USER_LOGIN" -> "Kullanıcı sisteme giriş yaptı.";
                default -> action + " işlemi başarıyla tamamlandı.";
            };
        } else {
            return switch (action) {
                case "USER_CREATED" -> "Kullanıcı kayıt denemesi başarısız oldu!";
                case "USER_LOGIN" -> "Hatalı giriş denemesi yapıldı!";
                default -> action + " işlemi sırasında bir hata oluştu!";
            };
        }
    }

}
