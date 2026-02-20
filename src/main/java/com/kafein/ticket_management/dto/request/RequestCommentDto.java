package com.kafein.ticket_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RequestCommentDto {

    @NotBlank(message = "Yorum içeriği boş olamaz")
    @Size(min = 2, max = 500, message = "Yorum 2 ile 500 karakter arasında olmalıdır")
    private String content;

    public RequestCommentDto() {
    }

    public RequestCommentDto(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    } 

    
  
}
