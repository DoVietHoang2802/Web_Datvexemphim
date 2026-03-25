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

    /** Vé đang rao bán - load đủ thông tin cho market */
    @org.springframework.data.jpa.repository.EntityGraph(
        attributePaths = {"showtime.movie", "showtime.room", "seat", "owner"}
    )
    @Query("SELECT t FROM Ticket t WHERE t.status = :status")
    List<Ticket> findByStatus(@Param("status") TicketStatus status);

    /** Vé của 1 user theo trạng thái - load đủ thông tin */
    @org.springframework.data.jpa.repository.EntityGraph(
        attributePaths = {"showtime.movie", "showtime.room", "seat", "owner"}
    )
    @Query("SELECT t FROM Ticket t WHERE t.owner.id = :ownerId AND t.status = :status")
    List<Ticket> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") TicketStatus status);

    @org.springframework.data.jpa.repository.EntityGraph(
        attributePaths = {"showtime", "seat", "owner", "payment"}
    )
    List<Ticket> findByShowtimeId(Long showtimeId);

    /** Đếm vé chưa bị hủy của 1 suất chiếu */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.showtime.id = :showtimeId AND t.status <> 'CANCELLED'")
    long countNonCancelledByShowtimeId(@Param("showtimeId") Long showtimeId);
}

