package com.kafein.ticket_management.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kafein.ticket_management.dto.response.ResponseReportDto;
import com.kafein.ticket_management.model.AuditLog;
import com.kafein.ticket_management.service.export.ExportFactory;
import com.kafein.ticket_management.service.export.ExportStrategy;

@Service
public class ReportService {

    private final AuditLogService auditLogService;
    private final ExportFactory exportFactory;

    public ReportService(AuditLogService auditLogService, ExportFactory exportFactory) {
        this.auditLogService = auditLogService;
        this.exportFactory = exportFactory;
    }

    public ResponseReportDto generateAuditLogReport(String query, String format) {
        // Veriyi çekme 
        List<AuditLog> logs = auditLogService.export(query);

        ExportStrategy strategy = exportFactory.getStrategy(format == null ? "EXCEL" : format);
        byte[] rawData;
        try (ByteArrayInputStream bis = strategy.export(logs)) {
            rawData = bis.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Veri okunurken hata oluştu", e);
        }

        return new ResponseReportDto(
                rawData,
                strategy.getContentType(),
                strategy.getFileExtension());
    }

}
