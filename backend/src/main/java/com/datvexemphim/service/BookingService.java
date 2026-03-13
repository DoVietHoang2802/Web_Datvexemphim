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

        List<Ticket> tickets = seats.stream().map(seat -> {
            Ticket t = new Ticket();
            t.setShowtime(showtime);
            t.setSeat(seat);
            t.setOwner(user);
            t.setStatus(TicketStatus.PENDING);
            return t;
        }).toList();

        ticketRepository.saveAll(tickets);

        long total = showtime.getPrice() * tickets.size();
        List<Long> ids = tickets.stream().map(Ticket::getId).toList();
        return new BookingResponse(ids, total);
    }
}

