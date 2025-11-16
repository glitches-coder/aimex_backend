package com.aimex.backend.controller;

import com.aimex.backend.models.Budget;
import com.aimex.backend.models.Category;
import com.aimex.backend.repository.BudgetRepository;
import com.aimex.backend.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CategoriesController {

    private final CategoryRepository categoryRepository;

    // Constructor Injection
    public CategoriesController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // GET all categories
    @GetMapping("/aimex/categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    // GET category by ID
    @GetMapping("/aimex/categories/{id}")
    public Optional<Category> getCategoryById(@PathVariable("id") String id) {
        return categoryRepository.findById(id);
    }

    // POST create new category
    @PostMapping("/aimex/categories")
    public void postCategory(@RequestBody Category category) {
        categoryRepository.save(category);
    }

    // DELETE category by ID
    @DeleteMapping("/aimex/categories/{id}")
    public void deleteCategory(@PathVariable("id") String id) {
        categoryRepository.deleteById(id);
    }

    // PUT update category
    @PutMapping("/aimex/categories/{id}")
    public void putCategory(@PathVariable("id") String id, @RequestBody Category category) {
        category.setId(id);
        categoryRepository.save(category);
    }
}