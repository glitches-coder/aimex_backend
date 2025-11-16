package com.aimex.backend.service.dto;

public class TrendPointDTO {
    public String month; // "2025-10"
    public double total;

    public TrendPointDTO(String month, double total) {
        this.month = month;
        this.total = total;
    }
}