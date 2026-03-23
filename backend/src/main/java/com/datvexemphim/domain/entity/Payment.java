package com.datvexemphim.domain.entity;

import com.datvexemphim.domain.enums.PaymentStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_user", columnList = "user_id"),
        @Index(name = "idx_payments_booking_code", columnList = "bookingCode", unique = true)
})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String provider = "SIMULATED";

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant paidAt;

    @Column(nullable = false, unique = true, length = 50)
    private String bookingCode;

    @Column(name = "has_food_order", nullable = false)
    private Boolean hasFoodOrder = false;

    @Column(name = "food_order_total")
    private java.math.BigDecimal foodOrderTotal;

    @Column(length = 100)
    private String transactionId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }
    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }
    public Boolean getHasFoodOrder() { return hasFoodOrder; }
    public void setHasFoodOrder(Boolean hasFoodOrder) { this.hasFoodOrder = hasFoodOrder; }
    public java.math.BigDecimal getFoodOrderTotal() { return foodOrderTotal; }
    public void setFoodOrderTotal(java.math.BigDecimal foodOrderTotal) { this.foodOrderTotal = foodOrderTotal; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}

