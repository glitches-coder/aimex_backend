package com.aimex.backend.service.dto;

public record BudgetAlertDTO(
        String categoryId,
        String monthYear,
        double spent,
        double limit,
        double percentUsed,
        String status
) { }

