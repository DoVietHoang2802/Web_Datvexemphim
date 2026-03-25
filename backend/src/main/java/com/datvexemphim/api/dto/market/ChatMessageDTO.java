package com.datvexemphim.api.dto.market;

import java.time.Instant;

public record ChatMessageDTO(
        Long id,
        Long ticketId,
        Long senderId,
        String senderFullName,
        String content,
        Instant createdAt
) {}
