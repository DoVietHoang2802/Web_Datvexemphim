package com.datvexemphim.api.dto.market;

public record NotificationCountDTO(
        long incomingRequests,   // yêu cầu nhận vé trên vé của tôi
        long myRequests          // yêu cầu nhận vé của tôi (chờ duyệt)
) {}
