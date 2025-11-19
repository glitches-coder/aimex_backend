package com.aimex.backend.service;


import com.aimex.backend.models.Category;
import com.aimex.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void validateCategory(String categoryId, String userId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        Optional<Category> category = getCategoryById(userId, categoryId);
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Category Not Found with ID: " + categoryId);
        }
    }

    public List<Category> getCategoriesByUser(String userId) {
        return categoryRepository.findByUserId(userId);
    }

    public Optional<Category> getCategoryById(String userId, String categoryId) {
        return categoryRepository.findById(categoryId)
                .filter(category -> category.getUserId().equals(userId));
    }

    public Category createCategory(String userId, Category category) {
        category.setUserId(userId);
        return categoryRepository.save(category);
    }

    public Category updateCategory(String userId, String id, Category category) {
        Optional<Category> existing = getCategoryById(userId, id);
        if (existing.isEmpty()) {
            throw new RuntimeException("Category not found or does not belong to this user");
        }
        category.setId(id);
        category.setUserId(userId);
        return categoryRepository.save(category);
    }

    public void deleteCategory(String userId, String id) {
        Optional<Category> existing = getCategoryById(userId, id);
        if (existing.isEmpty()) {
            throw new RuntimeException("Category not found or does not belong to this user");
        }
        categoryRepository.delete(existing.get());
    }
}
