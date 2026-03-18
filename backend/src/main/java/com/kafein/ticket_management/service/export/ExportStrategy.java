package com.kafein.ticket_management.service.export;

import java.io.ByteArrayInputStream;
import java.util.stream.Stream;

import com.kafein.ticket_management.model.AuditLog;

public interface ExportStrategy {
    ByteArrayInputStream export(Stream<AuditLog> logStream);
    String getContentType();
    String getFileExtension();
}
