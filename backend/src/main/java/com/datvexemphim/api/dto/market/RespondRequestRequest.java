package com.datvexemphim.api.dto.market;

/** Request body: đồng ý / từ chối yêu cầu nhận vé */
public record RespondRequestRequest(
        Long requestId,
        boolean accept   // true = đồng ý, false = từ chối
) {}
