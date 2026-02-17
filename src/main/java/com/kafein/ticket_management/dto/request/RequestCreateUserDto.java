package com.kafein.ticket_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RequestCreateUserDto { // TODO : Validasyon uygula

    private String name;
    private String surname;
    private String email;
    private String password;


    
}
