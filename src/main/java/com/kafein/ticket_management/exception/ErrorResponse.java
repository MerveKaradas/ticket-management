package com.kafein.ticket_management.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    
    
}
