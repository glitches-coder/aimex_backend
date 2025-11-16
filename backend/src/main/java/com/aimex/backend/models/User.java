package com.aimex.backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("users")
@Data
public class User {

    @Id
    private String id;

    private String email;
    private String passwordHash;

    private LocalDateTime createdAt;
}

