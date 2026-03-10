package com.kafein.ticket_management.dto.response;

import java.time.LocalDateTime;

import com.kafein.ticket_management.model.enums.AuditLogStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseAuditLogDto {
   private final Long id;
    private final String operation;    
    private final String details;    
    private final String errorMessage;    
    private final String performedBy;  
    private final LocalDateTime createdAtDate; 
    private final AuditLogStatus status;     
}