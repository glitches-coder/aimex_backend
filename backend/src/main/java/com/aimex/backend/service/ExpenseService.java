package com.aimex.backend.service;

import com.aimex.backend.models.Expense;
import com.aimex.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;
    private final AiCategorizationService aiCategorizationService;

    public ExpenseService(ExpenseRepository expenseRepository,
                          CategoryService categoryService,
                          AiCategorizationService aiCategorizationService) {
        this.expenseRepository = expenseRepository;
        this.categoryService = categoryService;
        this.aiCategorizationService = aiCategorizationService;
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
        return persistExpense(userId, expense);
    }

    public List<Expense> bulkCreateExpenses(String userId, List<Expense> expenses) {
        return expenses.stream()
                .map(expense -> persistExpense(userId, expense))
                .collect(Collectors.toList());
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

        Expense expenseToSave = existingExpense.get();
        updatedExpense.setId(id);
        updatedExpense.setUserId(userId);
        updatedExpense.setDate(updatedExpense.getDate() == null ? expenseToSave.getDate() : updatedExpense.getDate());

        if (updatedExpense.getCategoryId() != null) {
            categoryService.validateCategory(updatedExpense.getCategoryId(), userId);
        } else {
            applyAICategorization(userId, updatedExpense);
        }

        detectRecurring(userId, updatedExpense);

        return expenseRepository.save(updatedExpense);
    }

    private Expense persistExpense(String userId, Expense expense) {
        expense.setUserId(userId);

        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }

        if (expense.getCategoryId() != null) {
            categoryService.validateCategory(expense.getCategoryId(), userId);
        } else {
            applyAICategorization(userId, expense);
        }

        detectRecurring(userId, expense);

        return expenseRepository.save(expense);
    }

    private void applyAICategorization(String userId, Expense expense) {
        aiCategorizationService.suggestCategory(userId, expense)
                .ifPresent(suggestion -> {
                    expense.setCategoryId(suggestion.categoryId());
                    expense.setConfidenceScore(suggestion.confidence());
                    expense.setAiReasoning(suggestion.reason());
                });
    }

    private void detectRecurring(String userId, Expense expense) {
        if (expense.getMerchant() == null || expense.getDate() == null || expense.getAmount() == null) {
            expense.setIsRecurring(false);
            return;
        }

        List<Expense> history = expenseRepository
                .findByUserIdAndMerchantIgnoreCase(userId, expense.getMerchant());

        long similar = history.stream()
                .filter(existing -> existing.getDate() != null)
                .filter(existing -> ChronoUnit.DAYS.between(existing.getDate(), expense.getDate()) <= 90)
                .filter(existing -> Math.abs(existing.getAmount() - expense.getAmount()) <= expense.getAmount() * 0.1)
                .count();

        expense.setIsRecurring(similar >= 2);
    }
}
