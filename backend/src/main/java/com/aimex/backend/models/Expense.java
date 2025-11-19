package com.aimex.backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("expenses")
@CompoundIndexes({
        @CompoundIndex(name = "user_date_idx", def = "{'userId': 1, 'date': 1}"),
        @CompoundIndex(name = "user_merchant_idx", def = "{'userId': 1, 'merchant': 1}")
})
@Data
public class Expense {
    @Id
    private String id;

    @Indexed
    private String userId;           // Optional now; future multi-user feature
    private Double amount;
    @Indexed
    private String merchant;
    private String description;

    private String categoryId;         // AI suggested or user-chosen

    @Indexed
    private LocalDate date;
    private String paymentMethod;    // UPI, Card, Cash, Wallet, etc.

    private Boolean isRecurring;

    // For analytics & optimization
    private Double confidenceScore;   // AI categorization confidence
    private String aiReasoning;       // short reason from AI

}
