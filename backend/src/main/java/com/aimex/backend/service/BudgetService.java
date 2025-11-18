package com.aimex.backend.service;


import com.aimex.backend.models.Budget;
import com.aimex.backend.models.Expense;
import com.aimex.backend.repository.BudgetRepository;
import com.aimex.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public Map<String, String> getBudgetAlerts(String userId) {

        List<Budget> budgets = getBudgets(userId);

        Map<String, String> alerts = new HashMap<>();

        for (Budget budget : budgets) {

            YearMonth budgetMonth = YearMonth.parse(budget.getMonthYear());
            LocalDate budgetStartDate = budgetMonth.atDay(1);
            LocalDate budgetEndDate = budgetMonth.atEndOfMonth();

            double spent = expenseRepository.findByUserIdAndDateBetween(userId, budgetStartDate, budgetEndDate)
                    .stream()
                    .filter(e -> e.getCategoryId() != null)
                    .filter(e -> e.getCategoryId().equals(budget.getCategoryId()))
                    .mapToDouble(Expense::getAmount)
                    .sum();

            double percent = (spent / budget.getMonthlyLimit()) * 100.0;

            String status;
            if (percent < 70) {
                status = "green";      // Safe zone
            } else if (percent < 90) {
                status = "yellow";     // Warning
            } else {
                status = "red";        // Critical
            }

            alerts.put(budget.getCategoryId(), status);
        }

        return alerts;
    }
}
