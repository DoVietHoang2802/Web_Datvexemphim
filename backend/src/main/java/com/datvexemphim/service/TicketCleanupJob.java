package com.datvexemphim.service;

import com.datvexemphim.domain.entity.Seat;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.enums.TicketStatus;
import com.datvexemphim.domain.repository.SeatRepository;
import com.datvexemphim.domain.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Job chạy mỗi phút để hủy các vé PENDING quá 10 phút chưa thanh toán.
 * Đồng thời giải phóng ghế đã bị giữ.
 */
@Component
public class TicketCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(TicketCleanupJob.class);
    private static final int PENDING_TIMEOUT_MINUTES = 10;

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;

    public TicketCleanupJob(TicketRepository ticketRepository, SeatRepository seatRepository) {
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
    }

    /**
     * Chạy mỗi 60 giây.
     * Tìm tất cả vé PENDING mà bookedAt cách đây > 10 phút → hủy + giải phóng ghế.
     */
    @Scheduled(fixedRate = 60_000) // mỗi 1 phút
    @Transactional
    public void cancelExpiredPendingTickets() {
        Instant threshold = Instant.now().minus(PENDING_TIMEOUT_MINUTES, ChronoUnit.MINUTES);

        List<Ticket> expiredTickets = ticketRepository.findByStatusAndBookedAtBefore(TicketStatus.PENDING, threshold);

        if (expiredTickets.isEmpty()) {
            return;
        }

        log.info("[TicketCleanup] Đang hủy {} vé PENDING quá hạn (>{})",
                expiredTickets.size(), PENDING_TIMEOUT_MINUTES + " phút");

        for (Ticket t : expiredTickets) {
            // Giải phóng ghế
            Seat seat = t.getSeat();
            if (seat != null) {
                seat.setActive(true);
                seatRepository.save(seat);
                log.debug("[TicketCleanup] Giải phóng ghế {} cho vé {}",
                        seat.getSeatCode(), t.getId());
            }

            // Hủy vé
            t.setStatus(TicketStatus.CANCELLED);
            t.setCancelledAt(Instant.now());
            ticketRepository.save(t);

            log.debug("[TicketCleanup] Đã hủy vé ID={}, ghế={}",
                    t.getId(), seat != null ? seat.getSeatCode() : "N/A");
        }

        log.info("[TicketCleanup] Hoàn tất. Đã hủy {} vé PENDING quá hạn.", expiredTickets.size());
    }
}
