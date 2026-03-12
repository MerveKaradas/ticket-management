package com.kafein.ticket_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record RequestLoginDto (

    @Schema(example = "kafein@gmail.com")
    @Email(message = "Geçerli bir email adresi giriniz")
    @NotBlank(message = "Email boş olamaz")
    String email,

    @Schema(example = "Password123!")
    @NotBlank(message = "Parola boş olamaz!")
    String password
    
) {}
