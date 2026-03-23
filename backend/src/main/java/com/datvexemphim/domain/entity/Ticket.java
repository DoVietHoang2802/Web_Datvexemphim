package com.datvexemphim.domain.entity;

import com.datvexemphim.domain.enums.TicketStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tickets",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ticket_showtime_seat", columnNames = {"showtime_id", "seat_id"})
        },
        indexes = {
                @Index(name = "idx_tickets_user", columnList = "user_id"),
                @Index(name = "idx_tickets_showtime", columnList = "showtime_id")
        })
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status = TicketStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private Instant bookedAt = Instant.now();

    private Instant cancelledAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Showtime getShowtime() { return showtime; }
    public void setShowtime(Showtime showtime) { this.showtime = showtime; }
    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public Instant getBookedAt() { return bookedAt; }
    public void setBookedAt(Instant bookedAt) { this.bookedAt = bookedAt; }
    public Instant getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Instant cancelledAt) { this.cancelledAt = cancelledAt; }
}

