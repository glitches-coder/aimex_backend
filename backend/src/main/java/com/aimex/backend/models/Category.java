package com.aimex.backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("categories")
@Data
public class Category {

    @Id
    private String id;
    private String userId;

    private String name;            // Food, Travel, Shopping, etc.
    private String color;           // UI color hex
    private String icon;            // Material icon name
}

