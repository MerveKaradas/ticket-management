package com.kafein.ticket_management.dto.response;

import java.io.ByteArrayInputStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseReportDto {
    private final ByteArrayInputStream rawData;
    private final String contentType; 
    private final String fileName;
    
}
