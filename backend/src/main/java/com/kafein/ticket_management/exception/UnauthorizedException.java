package com.kafein.ticket_management.exception;


public class UnauthorizedException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Bu işlem için oturum açmanız gerekiyor.";

    public UnauthorizedException() {
        super(DEFAULT_MESSAGE);
    }



    


    
}
