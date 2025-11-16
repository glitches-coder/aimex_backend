package com.aimex.backend.controller;

import com.aimex.backend.models.Budget;
import com.aimex.backend.models.Expense;
import com.aimex.backend.repository.BudgetRepository;
import com.aimex.backend.service.BudgetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class BudgetsController {

    private final BudgetService budgetService;
    public BudgetsController(BudgetRepository budgetRepository, BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/aimex/{userId}/budgets")
    public List<Budget> getBudgets(@PathVariable String userId) {
        return budgetService.getBudgets(userId);
    }

    @PostMapping("/aimex/{userId}/budgets")
    public Budget createBudget(@PathVariable String userId, @RequestBody Budget budget) {
        return budgetService.createBudget(userId, budget);
    }

    @PutMapping("/aimex/{userId}/budgets/{id}")
    public Budget updateBudget(@PathVariable String userId, @PathVariable String id, @RequestBody Budget budget) {
        return budgetService.updateBudget(userId, id, budget);
    }

    @DeleteMapping("/aimex/{userId}/budgets/{id}")
    public void deleteBudget(@PathVariable String id, @PathVariable String userId) {
        budgetService.deleteBudget(userId, id);
    }

    @GetMapping("/aimex/{userId}/budgets/alerts")
    public Map<String, String> getAlerts(@PathVariable String userId) {
        return budgetService.getBudgetAlerts(userId);
    }
}
