package com.aimex.backend.repository;

import com.aimex.backend.models.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends MongoRepository<Budget,String> {
    List<Budget> findAllByUserId(String userId);
}
