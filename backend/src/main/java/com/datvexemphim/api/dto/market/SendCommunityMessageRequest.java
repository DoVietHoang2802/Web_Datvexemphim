package com.datvexemphim.api.dto.market;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendCommunityMessageRequest(
        @NotBlank @Size(max = 1000) String content
) {}
