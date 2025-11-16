package com.aimex.backend.service.dto;
import lombok.Data;

import java.util.Map;

@Data
public class MonthlySummaryDTO {
    public double totalSpent;
    public Map<String, Double> categoryTotals;
    public double lastMonthTotal;

    public MonthlySummaryDTO(double totalSpent, Map<String, Double> categoryTotals, double lastMonthTotal) {
        this.totalSpent = totalSpent;
        this.categoryTotals = categoryTotals;
        this.lastMonthTotal = lastMonthTotal;
    }
}
