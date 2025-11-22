package com.aimex.backend.service;


import com.aimex.backend.models.Budget;
import com.aimex.backend.models.Expense;
import com.aimex.backend.repository.BudgetRepository;
import com.aimex.backend.repository.ExpenseRepository;
import com.aimex.backend.service.dto.BudgetAlertDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(BudgetRepository budgetRepository, ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    public List<Budget> getBudgets(String userId) {
        return budgetRepository.findAllByUserId(userId);
    }

    public Budget createBudget(String userId, Budget budget) {
        budget.setUserId(userId);
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(String userId, String id, Budget updated) {

        Optional<Budget> existing = getBudgetById(userId, id);

        if (existing.isEmpty()) {
            throw new RuntimeException("Budget not found or does not belong to this user");
        }

        updated.setId(id);
        updated.setUserId(userId);
        return budgetRepository.save(updated);
    }

    public Optional<Budget> getBudgetById(String userId, String id) {
        return budgetRepository.findById(id)
                .filter(b -> b.getUserId().equals(userId));
    }

    public void deleteBudget(String userId, String id) {

        Optional<Budget> existing = getBudgetById(userId, id);

        if (existing.isEmpty()) {
            throw new RuntimeException("Budget not found or does not belong to this user");
        }

        budgetRepository.delete(existing.get());
    }

    public List<BudgetAlertDTO> getBudgetAlerts(String userId, String monthYear) {
        YearMonth targetMonth = resolveMonth(monthYear);
        List<Budget> budgets = budgetRepository.findAllByUserIdAndMonthYear(userId, targetMonth.toString());

        if (budgets.isEmpty()) {
            return List.of();
        }

        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();

        return budgets.stream()
                .map(budget -> buildAlert(userId, budget, start, end))
                .collect(Collectors.toList());
    }

    private BudgetAlertDTO buildAlert(String userId, Budget budget, LocalDate start, LocalDate end) {
        double spent = expenseRepository
                .findByUserIdAndCategoryIdAndDateBetween(userId, budget.getCategoryId(), start, end)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        double limit = Optional.ofNullable(budget.getMonthlyLimit()).orElse(0d);
        double percent = limit == 0 ? 0 : (spent / limit) * 100.0;

        return new BudgetAlertDTO(
                budget.getCategoryId(),
                budget.getMonthYear(),
                spent,
                limit,
                percent,
                determineStatus(percent)
        );
    }

    private String determineStatus(double percent) {
        if (percent < 70) {
            return "green";
        } else if (percent < 90) {
            return "yellow";
        }
        return "red";
    }

    private YearMonth resolveMonth(String monthYear) {
        if (monthYear == null || monthYear.isBlank()) {
            return YearMonth.now();
        }
        return YearMonth.parse(monthYear);
    }
}
