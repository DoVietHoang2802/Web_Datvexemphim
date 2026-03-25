package com.datvexemphim.domain.entity;

import com.datvexemphim.domain.enums.TicketRequestStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "ticket_requests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ticket_requester", columnNames = {"ticket_id", "requester_id"})
        },
        indexes = {
                @Index(name = "idx_ticket_request_ticket", columnList = "ticket_id"),
                @Index(name = "idx_ticket_request_requester", columnList = "requester_id")
        })
public class TicketRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketRequestStatus status = TicketRequestStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant respondedAt;

    // ===== Getters & Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public User getRequester() { return requester; }
    public void setRequester(User requester) { this.requester = requester; }

    public TicketRequestStatus getStatus() { return status; }
    public void setStatus(TicketRequestStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getRespondedAt() { return respondedAt; }
    public void setRespondedAt(Instant respondedAt) { this.respondedAt = respondedAt; }
}
