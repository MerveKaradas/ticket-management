package com.kafein.ticket_management.exception;

public class UserAlreadyExistsException extends RuntimeException{

    private static final String DEFAULT_MESSAGE  = "Bu kullanıcı kaydı zaten mevcut!";

    public UserAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    
    
}
