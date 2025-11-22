package com.aimex.backend.repository;

import com.aimex.backend.models.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category,String> {
    List<Category> findAllByUserId(String userId);
    List<Category> findByUserId(String userId);
}
