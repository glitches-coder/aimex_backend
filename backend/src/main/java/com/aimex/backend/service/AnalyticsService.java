package com.aimex.backend.service;

import com.aimex.backend.models.Expense;
import com.aimex.backend.repository.ExpenseRepository;
import com.aimex.backend.service.dto.CategoryBreakdownDTO;
import com.aimex.backend.service.dto.InsightDTO;
import com.aimex.backend.service.dto.MonthlySummaryDTO;
import com.aimex.backend.service.dto.TrendPointDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final ExpenseRepository expenseRepository;

    public AnalyticsService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public MonthlySummaryDTO getMonthlySummary(String userId) {
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);

        // returns a list of expenses within a range
        List<Expense> thisMonth = expenseRepository.findByUserIdAndDateBetween(userId, start, now);

        Map<String, Double> categoryTotals = thisMonth.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        double totalSpent = thisMonth.stream().mapToDouble(Expense::getAmount).sum();

        YearMonth lastMonthYM = YearMonth.now().minusMonths(1);
        LocalDate lastStart = lastMonthYM.atDay(1);
        LocalDate lastEnd = lastMonthYM.atEndOfMonth();
        
        List<Expense> lastMonth = expenseRepository.findByUserIdAndDateBetween(userId, lastStart, lastEnd);
        
        double lastMonthTotal = lastMonth.stream().mapToDouble(Expense::getAmount).sum();
        
        return  new MonthlySummaryDTO(totalSpent, categoryTotals, lastMonthTotal);
        
    }

    public List<CategoryBreakdownDTO> getCategoryBreakdown(String userId) {
        List<Expense> all = expenseRepository.findAllByUserId(userId);

        return all.stream()
                .collect(
                        Collectors.groupingBy(
                                Expense::getCategory,
                                Collectors.summingDouble(Expense::getAmount)
                        )
                )
                .entrySet()
                .stream()
                .map(c -> new CategoryBreakdownDTO(c.getKey(), c.getValue()))
                .collect(Collectors.toList());
    }

    public List<TrendPointDTO> getTrends(String userId) {
        List<TrendPointDTO> result = new ArrayList<>();

        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            LocalDate start = yearMonth.atDay(1);
            LocalDate end = yearMonth.atEndOfMonth();

            double total = expenseRepository.findByUserIdAndDateBetween(userId, start, end)
                    .stream().mapToDouble(Expense::getAmount).sum();

            result.add(new TrendPointDTO(yearMonth.toString(), total));
        }

        return result;

    }

    public List<InsightDTO> getAIInsights() {
        // type, title and message
        // yet to integrate logic
        // TODO - integrate gen ai llm here
        return List.of(
                new InsightDTO("info", "Spending Stable", "Your spending is consistent with last month."),
                new InsightDTO("warning", "High Dining Spend", "You spent 22% more on Dining this month.")
        );
    }
}
