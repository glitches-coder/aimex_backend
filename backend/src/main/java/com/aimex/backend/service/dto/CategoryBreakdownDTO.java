package com.aimex.backend.service.dto;

public class CategoryBreakdownDTO {
    public String category;
    public double total;

    public CategoryBreakdownDTO(String category, double total) {
        this.category = category;
        this.total = total;
    }
}