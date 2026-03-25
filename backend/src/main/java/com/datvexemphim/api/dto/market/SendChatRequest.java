package com.datvexemphim.api.dto.market;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Request body: gửi tin nhắn chat */
public record SendChatRequest(
        @NotNull Long ticketId,
        @NotBlank @Size(max = 1000) String content
) {}
