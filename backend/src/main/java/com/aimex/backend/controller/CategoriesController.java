package com.aimex.backend.controller;

import com.aimex.backend.models.Budget;
import com.aimex.backend.models.Category;
import com.aimex.backend.repository.BudgetRepository;
import com.aimex.backend.repository.CategoryRepository;
import com.aimex.backend.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CategoriesController {

    private final CategoryService categoryService;

    public CategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/aimex/{userId}/categories")
    public List<Category> getAll(@PathVariable String userId) {
        return categoryService.getCategoriesByUser(userId);
    }

    @PostMapping("/aimex/{userId}/categories")
    public Category create(@PathVariable String userId, @RequestBody Category category) {
        return categoryService.createCategory(userId, category);
    }

    @PutMapping("/aimex/{userId}/categories/{id}")
    public Category update(@PathVariable String userId, @PathVariable String id, @RequestBody Category category) {
        return categoryService.updateCategory(userId, id, category);
    }

    @DeleteMapping("/aimex/{userId}/categories/{id}")
    public void delete(@PathVariable String userId, @PathVariable String id) {
        categoryService.deleteCategory(userId, id);
    }
}


