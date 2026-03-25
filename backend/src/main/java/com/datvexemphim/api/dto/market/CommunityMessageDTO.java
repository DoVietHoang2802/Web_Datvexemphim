package com.datvexemphim.api.dto.market;

import java.time.Instant;

public record CommunityMessageDTO(
        Long id,
        Long senderId,
        String senderFullName,
        String content,
        Instant createdAt
) {}
