package com.datvexemphim.api.dto.admin;

import java.time.Instant;

public record AdminUserDto(
        Long id,
        String fullName,
        String email,
        String role,
        Instant createdAt,
        boolean enabled
) {
}

