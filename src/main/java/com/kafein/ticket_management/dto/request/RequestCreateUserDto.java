package com.kafein.ticket_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RequestCreateUserDto { 
    
    @NotBlank(message = "İsim alanı boş geçilemez")
    @Size(min=2, max=50, message = "İsim alanı 2 ile 50 karakter arasında olmalıdır")
    private String name;

    @NotBlank(message = "Soyad alanı boş geçilemez")
    @Size(min=2, max=50, message = "Soyad alanı 2 ile 75 karakter arasında olmalıdır")
    private String surname;

    @Email(message = "Geçerli bir email adresi giriniz")
    @NotBlank(message = "Email boş olamaz")
    private String email;

    @NotBlank(message = "Parola boş olamaz!")
    @Size(min = 8, max = 100, message = "Parola 8-100 karakter arasında olmalıdır!")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}",
        message = "Parola en az bir büyük harf, bir küçük harf, bir sayı ve bir özel karakter içermeli"
    )
    private String password;


    
}
