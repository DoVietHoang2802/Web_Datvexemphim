package com.datvexemphim.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "food_order", indexes = {
    @Index(name = "idx_payment", columnList = "payment_id"),
    @Index(name = "idx_ticket", columnList = "ticket_id"),
    @Index(name = "idx_original_buyer", columnList = "original_buyer_id"),
    @Index(name = "idx_current_owner", columnList = "current_owner_id"),
    @Index(name = "idx_status", columnList = "food_order_status"),
    @Index(name = "idx_created", columnList = "created_at")
})
public class FoodOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "original_buyer_id", nullable = false)
    private User originalBuyer;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "current_owner_id", nullable = false)
    private User currentOwner;
    
    @Column(name = "transfer_history_id")
    private Long transferHistoryId;
    
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "food_order_status", nullable = false)
    private FoodOrderStatus foodOrderStatus = FoodOrderStatus.PENDING;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "foodOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<FoodOrderItem> items;
    
    // JPA Callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Enum for Food Order Status
    public enum FoodOrderStatus {
        PENDING("Chờ thanh toán"),
        CONFIRMED("Đã xác nhận"),
        CANCELLED("Đã hủy");
        
        private final String displayName;
        
        FoodOrderStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    
    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
    
    public User getOriginalBuyer() { return originalBuyer; }
    public void setOriginalBuyer(User originalBuyer) { this.originalBuyer = originalBuyer; }
    
    public User getCurrentOwner() { return currentOwner; }
    public void setCurrentOwner(User currentOwner) { this.currentOwner = currentOwner; }
    
    public Long getTransferHistoryId() { return transferHistoryId; }
    public void setTransferHistoryId(Long transferHistoryId) { this.transferHistoryId = transferHistoryId; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public FoodOrderStatus getFoodOrderStatus() { return foodOrderStatus; }
    public void setFoodOrderStatus(FoodOrderStatus foodOrderStatus) { this.foodOrderStatus = foodOrderStatus; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Set<FoodOrderItem> getItems() { return items; }
    public void setItems(Set<FoodOrderItem> items) { this.items = items; }
}
