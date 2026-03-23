package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByOwnerIdOrderByBookedAtDesc(Long ownerId);

    List<Ticket> findByStatusAndBookedAtBefore(TicketStatus status, Instant threshold);

    @Query("""
            select t.seat.id from Ticket t
            where t.showtime.id = :showtimeId
              and t.status <> com.datvexemphim.domain.enums.TicketStatus.CANCELLED
            """)
    List<Long> findBookedSeatIds(@Param("showtimeId") Long showtimeId);

    List<Ticket> findByIdIn(Collection<Long> ids);

    long countByShowtimeIdAndStatus(Long showtimeId, TicketStatus status);
}

