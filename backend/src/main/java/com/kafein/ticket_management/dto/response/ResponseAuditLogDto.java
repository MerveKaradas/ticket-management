package com.kafein.ticket_management.dto.response;

import java.time.LocalDateTime;

import com.kafein.ticket_management.model.enums.AuditLogStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAuditLogDto {
   private Long id;
    private String operation;    
    private String details;    
    private String errorMessage;    
    private String performedBy;  
    private LocalDateTime createdAtDate; 
    private AuditLogStatus status;     
}