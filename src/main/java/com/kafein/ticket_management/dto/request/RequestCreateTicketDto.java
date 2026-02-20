package com.kafein.ticket_management.dto.request;

import java.util.UUID;

import com.kafein.ticket_management.model.enums.TicketPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreateTicketDto {

    @NotBlank(message = "Bilet başlığı boş olamaz")
    @Size(min = 5, max = 150, message = "Başlık 5-150 karakter arasında olmalıdır")
    private String title;

    @Size(max = 2000, message = "Açıklama çok uzun")
    private String description;

    @NotNull(message = "Öncelik seviyesi belirtilmelidir")
    private TicketPriority priority;

    @NotNull(message = "Atanacak kullanıcıyı belirleme zorunludur")
    private UUID assignedToId;
    
}
