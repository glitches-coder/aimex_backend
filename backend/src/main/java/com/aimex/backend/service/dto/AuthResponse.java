package com.aimex.backend.service.dto;

public record AuthResponse(String token, String userId, String email) {
}

