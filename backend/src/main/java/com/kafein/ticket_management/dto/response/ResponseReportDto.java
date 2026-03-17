package com.kafein.ticket_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseReportDto {
    private final byte[] rawData;
    private final String contentType; 
    private final String fileExtension;
    
}
