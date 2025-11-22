package com.aimex.backend.repository;

import com.aimex.backend.models.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense,String> {
    List<Expense> findByDateBetween(LocalDate start, LocalDate end);
    List<Expense> findByUserIdAndDateBetween(String userId, LocalDate start, LocalDate end);
    List<Expense> findByUserIdAndCategoryIdAndDateBetween(String userId, String categoryId, LocalDate start, LocalDate end);
    List<Expense> findByUserIdAndMerchantIgnoreCase(String userId, String merchant);

    List<Expense> findAllByUserId(String userId);

}
