package com.aimex.backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("budgets")
@Data
public class Budget {

    @Id
    private String id;

    private String userId;
    private String categoryName;

    private Double monthlyLimit;
    private Double currentSpent;

    private String monthYear;   // "2025-11"
}

