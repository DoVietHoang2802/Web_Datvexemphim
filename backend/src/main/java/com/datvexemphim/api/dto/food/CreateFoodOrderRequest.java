package com.datvexemphim.api.dto.food;

import java.util.List;

public class CreateFoodOrderRequest {
    
    private Long paymentId;
    private Long ticketId;
    private List<FoodOrderItemRequest> items;
    private String notes;
    
    public CreateFoodOrderRequest() {}
    
    public CreateFoodOrderRequest(Long paymentId, Long ticketId, List<FoodOrderItemRequest> items, String notes) {
        this.paymentId = paymentId;
        this.ticketId = ticketId;
        this.items = items;
        this.notes = notes;
    }
    
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    
    public List<FoodOrderItemRequest> getItems() { return items; }
    public void setItems(List<FoodOrderItemRequest> items) { this.items = items; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public static class FoodOrderItemRequest {
        private Long foodItemId;
        private Integer quantity;
        
        public FoodOrderItemRequest() {}
        
        public FoodOrderItemRequest(Long foodItemId, Integer quantity) {
            this.foodItemId = foodItemId;
            this.quantity = quantity;
        }
        
        public Long getFoodItemId() { return foodItemId; }
        public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
