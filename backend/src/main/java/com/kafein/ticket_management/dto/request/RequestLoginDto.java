package com.kafein.ticket_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RequestLoginDto (

    @Schema(example = "kafein@gmail.com")
    @Email(message = "Geçerli bir email adresi giriniz")
    @NotBlank(message = "Email boş olamaz")
    String email,

    @Schema(example = "Password123!")
    @NotBlank(message = "Parola boş olamaz!")
    @Size(min = 8, max = 100, message = "Parola 8-100 karakter arasında olmalıdır!")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}",
        message = "Parola en az bir büyük harf, bir küçük harf, bir sayı ve bir özel karakter içermeli")
    String password
    
) {}
