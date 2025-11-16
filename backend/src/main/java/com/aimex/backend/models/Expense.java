package com.aimex.backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("expenses")
@Data
public class Expense {
    @Id
    private String id;

    private String userId;           // Optional now; future multi-user feature
    private Double amount;
    private String merchant;
    private String description;

    private String categoryId;         // AI suggested or user-chosen

    private LocalDate date;
    private String paymentMethod;    // UPI, Card, Cash, Wallet, etc.

    private Boolean isRecurring;

    // For analytics & optimization
    private Double confidenceScore;   // AI categorization confidence
    private String aiReasoning;       // short reason from AI

}
