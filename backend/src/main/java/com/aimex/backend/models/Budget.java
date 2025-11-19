package com.aimex.backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("budgets")
@CompoundIndex(name = "user_month_category_idx", def = "{'userId': 1, 'monthYear': 1, 'categoryId': 1}")
@Data
public class Budget {

    @Id
    private String id;

    @Indexed
    private String userId;
    @Indexed
    private String categoryId;

    private Double monthlyLimit;

    @Indexed
    private String monthYear;   // "2025-11"
}

