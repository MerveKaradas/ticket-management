package com.kafein.ticket_management.dto.request;



public class RequestCommentDto {
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
