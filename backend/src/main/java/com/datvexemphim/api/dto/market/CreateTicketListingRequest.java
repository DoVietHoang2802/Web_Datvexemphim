package com.datvexemphim.api.dto.market;

import jakarta.validation.constraints.NotNull;

/** Request body: đăng vé lên chợ */
public record CreateTicketListingRequest(
        @NotNull Long ticketId
) {}
