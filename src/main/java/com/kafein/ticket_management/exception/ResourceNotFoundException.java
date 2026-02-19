package com.kafein.ticket_management.exception;

public class ResourceNotFoundException extends RuntimeException{
    
    private String message;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        this.message = String.format("%s, %s ile bulunamadı : '%s'", resourceName, fieldName, fieldValue);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    
    
}
