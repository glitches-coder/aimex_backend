package com.aimex.backend.service.dto;

public record AiCategorySuggestion(
        String categoryId,
        String categoryName,
        double confidence,
        String reason
) { }

