package com.datvexemphim.api.dto.food;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FoodOrderDTO {
    private Long id;
    private Long paymentId;
    private Long ticketId;
    private Long originalBuyerId;
    private String originalBuyerName;
    private Long currentOwnerId;
    private String currentOwnerName;
    private Long transferHistoryId;
    private BigDecimal totalPrice;
    private String foodOrderStatus;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FoodOrderItemDTO> items;
    
    public FoodOrderDTO() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    
    public Long getOriginalBuyerId() { return originalBuyerId; }
    public void setOriginalBuyerId(Long originalBuyerId) { this.originalBuyerId = originalBuyerId; }
    
    public String getOriginalBuyerName() { return originalBuyerName; }
    public void setOriginalBuyerName(String originalBuyerName) { this.originalBuyerName = originalBuyerName; }
    
    public Long getCurrentOwnerId() { return currentOwnerId; }
    public void setCurrentOwnerId(Long currentOwnerId) { this.currentOwnerId = currentOwnerId; }
    
    public String getCurrentOwnerName() { return currentOwnerName; }
    public void setCurrentOwnerName(String currentOwnerName) { this.currentOwnerName = currentOwnerName; }
    
    public Long getTransferHistoryId() { return transferHistoryId; }
    public void setTransferHistoryId(Long transferHistoryId) { this.transferHistoryId = transferHistoryId; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public String getFoodOrderStatus() { return foodOrderStatus; }
    public void setFoodOrderStatus(String foodOrderStatus) { this.foodOrderStatus = foodOrderStatus; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<FoodOrderItemDTO> getItems() { return items; }
    public void setItems(List<FoodOrderItemDTO> items) { this.items = items; }
}
