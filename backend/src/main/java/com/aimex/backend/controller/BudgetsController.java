package com.aimex.backend.controller;

import com.aimex.backend.models.Budget;
import com.aimex.backend.service.BudgetService;
import com.aimex.backend.service.dto.BudgetAlertDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BudgetsController {

    private final BudgetService budgetService;
    public BudgetsController(BudgetService budgetService) {
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
    public List<BudgetAlertDTO> getAlerts(@PathVariable String userId,
                                          @RequestParam(value = "monthYear", required = false) String monthYear) {
        return budgetService.getBudgetAlerts(userId, monthYear);
    }
}
