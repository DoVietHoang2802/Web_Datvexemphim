package com.datvexemphim.api.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record FoodCategoryUpsertRequest(
    @NotBlank(message = "Tên danh mục không được trống")
    String name,

    String description,

    String icon,

    Integer displayOrder,

    Boolean isActive
) {
    public FoodCategoryUpsertRequest {
        if (displayOrder == null) displayOrder = 0;
        if (isActive == null) isActive = true;
    }
}
