package com.aimex.backend.service.dto;


public class InsightDTO {
    public String type;
    public String title;
    public String message;

    public InsightDTO(String type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
    }
}
