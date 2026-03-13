package com.datvexemphim.api.dto.ticket;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransferTicketRequest(
        @NotNull Long ticketId,
        @NotBlank @Email String toEmail
) {
}

