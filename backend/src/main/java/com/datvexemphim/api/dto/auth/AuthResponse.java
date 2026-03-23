package com.datvexemphim.api.dto.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String fullName,
        String email,
        String role
) {
    public static AuthResponse bearer(String token, Long userId, String fullName, String email, String role) {
        return new AuthResponse(token, "Bearer", userId, fullName, email, role);
    }
}

