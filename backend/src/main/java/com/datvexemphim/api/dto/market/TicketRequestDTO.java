package com.datvexemphim.api.dto.market;

import com.datvexemphim.domain.enums.TicketRequestStatus;

import java.time.Instant;

public record TicketRequestDTO(
        Long id,
        Long ticketId,
        Long requesterId,
        String requesterFullName,
        TicketRequestStatus status,
        Instant createdAt
) {}
