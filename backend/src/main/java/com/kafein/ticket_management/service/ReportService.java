package com.kafein.ticket_management.service;

import java.io.ByteArrayInputStream;
import java.util.stream.Stream;

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

        try (Stream<AuditLog> logStream = auditLogService.streamAll(query)) {

            ExportStrategy strategy = exportFactory.getStrategy(format);

            ByteArrayInputStream stream = strategy.export(logStream);

            String filename = "audit_logs_" + System.currentTimeMillis() + strategy.getFileExtension();
            return new ResponseReportDto(stream, strategy.getContentType(), filename);
        }
    }

}
