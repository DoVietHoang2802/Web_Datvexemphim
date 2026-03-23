package com.datvexemphim.api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record FoodItemUpsertRequest(
    @NotNull(message = "categoryId không được trống")
    Long categoryId,

    @NotBlank(message = "Tên sản phẩm không được trống")
    String name,

    String description,

    @NotNull(message = "Giá không được trống")
    @Positive(message = "Giá phải lớn hơn 0")
    BigDecimal price,

    String size,

    String imageUrl,

    Integer stock,

    Boolean isAvailable,

    Integer displayOrder
) {
    public FoodItemUpsertRequest {
        if (stock == null) stock = 0;
        if (isAvailable == null) isAvailable = true;
        if (displayOrder == null) displayOrder = 0;
    }
}
