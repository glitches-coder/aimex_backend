package com.aimex.backend.controller;

import com.aimex.backend.models.Budget;
import com.aimex.backend.models.Expense;
import com.aimex.backend.repository.BudgetRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class BudgetsController {

    private final BudgetRepository budgetRepository;
    public BudgetsController(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @GetMapping("/aimex/budgets")
    public List<Budget> getBudgets(){
        return budgetRepository.findAll();
    }

    @GetMapping("/aimex/budgets/{id}")
    public Optional<Budget> getBudgetsById(@PathVariable("id") String id){
        return budgetRepository.findById(id);
    }

    @PostMapping("/aimex/budgets")
    public void postBudgets(@RequestBody Budget budget){
        budgetRepository.save(budget);
    }

    @DeleteMapping("/aimex/budgets/{id}")
    public void deleteBudgetsById(@PathVariable("id") String id){
        budgetRepository.deleteById(id);
    }

    @PutMapping("/aimex/budgets/{id}")
    public void putBudgetsById(@PathVariable("id") String id, @RequestBody Budget budget){
        budget.setId(id);
        budgetRepository.save(budget);
    }
}
