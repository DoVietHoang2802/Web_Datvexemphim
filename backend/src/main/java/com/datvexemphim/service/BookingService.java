package com.datvexemphim.service;

import com.datvexemphim.api.dto.booking.BookingResponse;
import com.datvexemphim.api.dto.booking.CreateBookingRequest;
import com.datvexemphim.domain.entity.Seat;
import com.datvexemphim.domain.entity.Showtime;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.enums.TicketStatus;
import com.datvexemphim.domain.repository.SeatRepository;
import com.datvexemphim.domain.repository.ShowtimeRepository;
import com.datvexemphim.domain.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

@Service
public class BookingService {
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    public BookingService(ShowtimeRepository showtimeRepository, SeatRepository seatRepository, TicketRepository ticketRepository) {
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public BookingResponse createPendingTickets(CreateBookingRequest req, User user) {
        Showtime showtime = showtimeRepository.findById(req.showtimeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found"));
        if (!showtime.getStartTime().isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Showtime đã bắt đầu hoặc đã qua.");
        }

        List<Long> seatIds = req.seatIds().stream().distinct().toList();
        if (seatIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chưa chọn ghế.");
        }

        List<Seat> seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ghế không hợp lệ.");
        }
        Long roomId = showtime.getRoom().getId();
        boolean anyWrongRoom = seats.stream().anyMatch(s -> !s.getRoom().getId().equals(roomId));
        if (anyWrongRoom) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Có ghế không thuộc phòng của suất chiếu.");
        }

        // Optional early check (DB unique constraint is the final guard)
        var bookedSet = new HashSet<>(ticketRepository.findBookedSeatIds(showtime.getId()));
        boolean anyBooked = seats.stream().anyMatch(s -> bookedSet.contains(s.getId()));
        if (anyBooked) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Một hoặc nhiều ghế đã được đặt.");
        }

        int totalSeats = seats.size();
        int freebies = totalSeats / 3;  // Mua 3 tặng 1: cứ 3 vé → 1 vé free
        int paidSeats = totalSeats - freebies;
        long pricePerSeat = showtime.getPrice();
        long total = pricePerSeat * paidSeats;  // Chỉ tính tiền vé mua, vé tặng = 0

        // Ghế đầu tiên = vé mua, ghế cuối = vé tặng
        List<Ticket> tickets = seats.stream().map((Seat seat) -> {
            Ticket t = new Ticket();
            t.setShowtime(showtime);
            t.setSeat(seat);
            t.setOwner(user);
            t.setStatus(TicketStatus.PENDING);
            return t;
        }).toList();

        // Đánh dấu vé tặng (ghế cuối = freebie)
        for (int i = 0; i < freebies; i++) {
            Ticket free = tickets.get(tickets.size() - 1 - i);
            free.setPrice(0L);
            free.setFreebie(true);
        }

        // Vé mua giữ nguyên giá
        for (int i = 0; i < paidSeats; i++) {
            tickets.get(i).setPrice(pricePerSeat);
            tickets.get(i).setFreebie(false);
        }

        ticketRepository.saveAll(tickets);

        List<Long> ids = tickets.stream().map(Ticket::getId).toList();
        String promoMsg = freebies > 0
                ? "🎉 Mua " + totalSeats + " vé, tặng " + freebies + " vé! Tiết kiệm " + (freebies * pricePerSeat) + "đ"
                : null;
        return new BookingResponse(ids, total, promoMsg);
    }
}

