package com.kafein.ticket_management.service.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kafein.ticket_management.model.AuditLog;

@Service("EXCEL")
public class ExcelExportService implements ExportStrategy {

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream export(Stream<AuditLog> logStream) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Audit Logs");

            // Tarih Formatı 
            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();

            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm:ss"));

            for (int i = 0; i < 7; i++) {
                sheet.setColumnWidth(i, 5000); 
            }

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = { "ID", "İşlem", "Kullanıcı", "Durum", "Detay", "Hata Mesajı", "Tarih" };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Veriler
            final int[] rowIdx = {1};
            logStream.forEach(log -> {
                Row row = sheet.createRow(rowIdx[0]++);
                row.createCell(0).setCellValue(log.getId().toString());
                row.createCell(1).setCellValue(log.getOperation());
                row.createCell(2).setCellValue(log.getPerformedBy());
                row.createCell(3).setCellValue(log.getStatus().toString());
                row.createCell(4).setCellValue(log.getDetails());
                row.createCell(5).setCellValue(log.getErrorMessage());
                Cell dateCell = row.createCell(6);
                if (log.getCreatedAtDate() != null) {
                    dateCell.setCellValue(log.getCreatedAtDate()); 
                    dateCell.setCellStyle(dateCellStyle); 
                }
            });
                      
            workbook.write(out);
            // Geçici dosya temizliği
            workbook.dispose();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Excel stream hatası: " + e.getMessage());
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