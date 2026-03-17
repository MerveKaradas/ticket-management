package com.kafein.ticket_management.service.export;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ExportFactory {

    private final Map<String, ExportStrategy> strategies;

    // Spring, IExportStrategyi implemente eden tüm beanleri bu Map içerisine otomatik doldurur
    public ExportFactory(Map<String, ExportStrategy> strategies) {
        this.strategies = strategies;
    }

    public ExportStrategy getStrategy(String type) {
        ExportStrategy strategy = strategies.get(type.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Desteklenmeyen dosya formatı: " + type);
        }
        return strategy;
    }
    
}
