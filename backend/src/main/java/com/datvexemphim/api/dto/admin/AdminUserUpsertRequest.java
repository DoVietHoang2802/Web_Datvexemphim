package com.datvexemphim.api.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminUserUpsertRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        String password, // optional on update
        @NotNull String role
) {
}

