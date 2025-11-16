package com.aimex.backend.service;

import com.aimex.backend.models.Expense;
import com.aimex.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;

    public ExpenseService(ExpenseRepository expenseRepository, CategoryService categoryService) {
        this.expenseRepository = expenseRepository;
        this.categoryService = categoryService;
    }

    // GET all expenses for a user
    public List<Expense> getAllExpenses(String userId){
        return expenseRepository.findAllByUserId(userId);
    }

    public Optional<Expense> getExpenseById(String userId, String id) {
        return expenseRepository.findById(id)
                .filter(expense -> expense.getUserId().equals(userId));
    }

    public Expense createExpense(String userId, Expense expense){
        expense.setUserId(userId);
        // TODO - Auto category stub (future LLM integration)
        applyAICategorization(expense);

        detectRecurring(expense);

        expense.setDate(expense.getDate() == null ? LocalDate.now() : expense.getDate());
        return expenseRepository.save(expense);
    }


    public void deleteExpense(String userId, String id) {
        Optional<Expense> expense = getExpenseById(userId, id);
        if(expense.isEmpty()){
            throw new RuntimeException("Expense not found");
        }
        expenseRepository.delete(expense.get());
    }

    public Expense updateExpense(String userId, String id, Expense updatedExpense) {
        Optional<Expense> existingExpense = getExpenseById(userId, id);

        if(existingExpense.isEmpty()){
            throw new RuntimeException("Expense not found");
        }
        updatedExpense.setId(id);
        categoryService.validateCategory(updatedExpense.getCategoryId(), userId);

        return expenseRepository.save(updatedExpense);
    }

    // AI categorization placeholder
    private void applyAICategorization(Expense expense) {
        // TODO: integrate OpenAI API
        // For now, do nothing; category is user provided
    }

    // Recurring detection placeholder
    private void detectRecurring(Expense expense) {
        // TODO: implement simple recurring logic later
    }
}
