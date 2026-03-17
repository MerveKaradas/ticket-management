package com.kafein.ticket_management.service.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.kafein.ticket_management.model.AuditLog;

@Service("EXCEL")
public class ExcelExportService implements ExportStrategy {

    @Override
    public ByteArrayInputStream export(List<AuditLog> logs) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Audit Logs");

            // Header 
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "İşlem", "Kullanıcı", "Durum", "Detay","Hata Mesajı", "Tarih"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Veriler
            int rowIdx = 1;
            for (AuditLog log : logs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(log.getId().toString());
                row.createCell(1).setCellValue(log.getOperation());
                row.createCell(2).setCellValue(log.getPerformedBy());
                row.createCell(3).setCellValue(log.getStatus().toString());
                row.createCell(4).setCellValue(log.getDetails());
                row.createCell(5).setCellValue(log.getErrorMessage());
                row.createCell(6).setCellValue(log.getCreatedAtDate().toString());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Excel oluşturma hatası: " + e.getMessage());
        }
    }

    @Override
    public String getContentType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String getFileExtension() {
        return ".xlsx";
    }
}