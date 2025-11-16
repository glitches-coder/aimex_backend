package com.aimex.backend.controller;

import com.aimex.backend.models.Expense;
import com.aimex.backend.repository.ExpenseRepository;
import com.aimex.backend.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ExpensesController {

    private final ExpenseService expenseService;
    public ExpensesController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/aimex/{userId}/expenses")
    public List<Expense> getExpenses(@PathVariable String userId){
        return expenseService.getAllExpenses(userId);
    }

    @GetMapping("/aimex/{userId}/expenses/{id}")
    public Optional<Expense> getExpensesById(@PathVariable("id") String id, @PathVariable String userId){
        return expenseService.getExpenseById(userId, id);
    }

    @PostMapping("/aimex/{userId}/expenses")
    public Expense postExpenses(@PathVariable("userId") String userId, @RequestBody Expense expense){
       return expenseService.createExpense(userId, expense);
    }

    @PutMapping("/aimex/{userId}/expenses/{id}")
    public Expense update(@PathVariable String userId, @PathVariable String id, @RequestBody Expense expense) {
        return expenseService.updateExpense(userId, id, expense);
    }

    @DeleteMapping("/aimex/{userId}/expenses/{id}")
    public void delete(@PathVariable String userId, @PathVariable String id) {
        expenseService.deleteExpense(userId, id);
    }
}
