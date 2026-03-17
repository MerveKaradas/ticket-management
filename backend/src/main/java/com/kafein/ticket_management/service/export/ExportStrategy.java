package com.kafein.ticket_management.service.export;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.kafein.ticket_management.model.AuditLog;

public interface ExportStrategy {
    ByteArrayInputStream export(List<AuditLog> logs);
    String getContentType();
    String getFileExtension();
}
