package com.aimex.backend.controller;

import com.aimex.backend.models.Expense;
import com.aimex.backend.repository.ExpenseRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ExpensesController {

    private final ExpenseRepository expenseRepository;
    public ExpensesController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @GetMapping("/aimex/expenses")
    public List<Expense> getExpenses(){
        return expenseRepository.findAll();
    }

    @GetMapping("/aimex/expenses/{id}")
    public Optional<Expense> getExpensesById(@PathVariable("id") String id){
        return expenseRepository.findById(id);
    }

    @PostMapping("/aimex/expenses")
    public void postExpenses(@RequestBody Expense expense){
        expenseRepository.save(expense);
    }

    @DeleteMapping("/aimex/expenses/{id}")
    public void deleteExpensesById(@PathVariable("id") String id){
        expenseRepository.deleteById(id);
    }

    @PutMapping("/aimex/expenses/{id}")
    public void putExpensesById(@PathVariable("id") String id, @RequestBody Expense expense){
        expense.setId(id);
        expenseRepository.save(expense);
    }
}
