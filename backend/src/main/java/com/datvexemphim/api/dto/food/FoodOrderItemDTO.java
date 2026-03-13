package com.datvexemphim.api.dto.food;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FoodOrderItemDTO {
    private Long id;
    private Long foodOrderId;
    private Long foodItemId;
    private String foodItemName;
    private Integer quantity;
    private BigDecimal priceAtOrder;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
    
    public FoodOrderItemDTO() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getFoodOrderId() { return foodOrderId; }
    public void setFoodOrderId(Long foodOrderId) { this.foodOrderId = foodOrderId; }
    
    public Long getFoodItemId() { return foodItemId; }
    public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }
    
    public String getFoodItemName() { return foodItemName; }
    public void setFoodItemName(String foodItemName) { this.foodItemName = foodItemName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(BigDecimal priceAtOrder) { this.priceAtOrder = priceAtOrder; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
