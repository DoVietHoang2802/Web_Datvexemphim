package com.datvexemphim.service.admin;

import com.datvexemphim.api.dto.admin.AdminTicketDto;
import com.datvexemphim.api.dto.admin.AdminTicketUpsertRequest;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.entity.Seat;
import com.datvexemphim.domain.entity.Showtime;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.entity.FoodOrder;
import com.datvexemphim.domain.enums.TicketStatus;
import com.datvexemphim.domain.repository.SeatRepository;
import com.datvexemphim.domain.repository.ShowtimeRepository;
import com.datvexemphim.domain.repository.TicketRepository;
import com.datvexemphim.domain.repository.UserRepository;
import com.datvexemphim.domain.repository.FoodOrderRepository;
import com.datvexemphim.domain.repository.TransferHistoryRepository;
import com.datvexemphim.domain.repository.TicketRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class AdminTicketService {
    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final TransferHistoryRepository transferHistoryRepository;
    private final TicketRequestRepository ticketRequestRepository;

    public AdminTicketService(TicketRepository ticketRepository,
                              ShowtimeRepository showtimeRepository,
                              SeatRepository seatRepository,
                              UserRepository userRepository,
                              FoodOrderRepository foodOrderRepository,
                              TransferHistoryRepository transferHistoryRepository,
                              TicketRequestRepository ticketRequestRepository) {
        this.ticketRepository = ticketRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.foodOrderRepository = foodOrderRepository;
        this.transferHistoryRepository = transferHistoryRepository;
        this.ticketRequestRepository = ticketRequestRepository;
    }

    @Transactional
    public List<AdminTicketDto> list() {
        return ticketRepository.findAll().stream()
                .map(t -> new AdminTicketDto(
                        t.getId(),
                        t.getStatus().name(),
                        t.getBookedAt(),
                        t.getShowtime().getId(),
                        t.getShowtime().getStartTime(),
                        t.getShowtime().getMovie().getTitle(),
                        t.getShowtime().getRoom().getName(),
                        t.getSeat().getSeatCode(),
                        t.getOwner().getEmail(),
                        t.getShowtime().getPrice(),
                        t.getPayment() == null ? null : t.getPayment().getBookingCode()
                ))
                .toList();
    }

    @Transactional
    public AdminTicketDto get(Long id) {
        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        return new AdminTicketDto(
                t.getId(),
                t.getStatus().name(),
                t.getBookedAt(),
                t.getShowtime().getId(),
                t.getShowtime().getStartTime(),
                t.getShowtime().getMovie().getTitle(),
                t.getShowtime().getRoom().getName(),
                t.getSeat().getSeatCode(),
                t.getOwner().getEmail(),
                t.getShowtime().getPrice(),
                t.getPayment() == null ? null : t.getPayment().getBookingCode()
        );
    }

    @Transactional
    public AdminTicketDto create(AdminTicketUpsertRequest req) {
        Showtime showtime = showtimeRepository.findById(req.showtimeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Showtime not found"));
        Seat seat = seatRepository.findById(req.seatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat not found"));
        User owner = userRepository.findById(req.ownerUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));
        if (!seat.getRoom().getId().equals(showtime.getRoom().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat không thuộc room của showtime.");
        }

        Ticket t = new Ticket();
        t.setShowtime(showtime);
        t.setSeat(seat);
        t.setOwner(owner);
        t.setStatus(TicketStatus.valueOf(req.status()));
        Ticket saved = ticketRepository.save(t);
        return get(saved.getId());
    }

    @Transactional
    public AdminTicketDto update(Long id, AdminTicketUpsertRequest req) {
        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        Showtime showtime = showtimeRepository.findById(req.showtimeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Showtime not found"));
        Seat seat = seatRepository.findById(req.seatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat not found"));
        User owner = userRepository.findById(req.ownerUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));
        if (!seat.getRoom().getId().equals(showtime.getRoom().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat không thuộc room của showtime.");
        }

        t.setShowtime(showtime);
        t.setSeat(seat);
        t.setOwner(owner);
        t.setStatus(TicketStatus.valueOf(req.status()));
        ticketRepository.save(t);
        return get(t.getId());
    }

    @Transactional
    public void delete(Long id) {
        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        // 1. GIẢI PHÓNG GHẾ
        Seat seat = t.getSeat();
        if (seat != null) {
            seat.setActive(true);
            seatRepository.save(seat);
        }

        // 2. Xóa ticket request trước (quan trọng - tránh 409 conflict)
        try {
            ticketRequestRepository.deleteByTicketId(id);
            ticketRequestRepository.flush();
        } catch (Exception ex) {
            System.err.println("Failed to delete ticket requests for ticket " + id + ": " + ex.getMessage());
        }

        // 3. Delete transfer history records linked to this ticket
        try {
            List<com.datvexemphim.domain.entity.TransferHistory> transfers = transferHistoryRepository.findByTicketId(id);
            if (!transfers.isEmpty()) {
                transferHistoryRepository.deleteAll(transfers);
                transferHistoryRepository.flush();
            }
        } catch (Exception ex) {
            System.err.println("Failed to delete transfer history for ticket " + id + ": " + ex.getMessage());
        }

        // 4. Delete food orders linked to this ticket
        try {
            List<FoodOrder> foodOrders = foodOrderRepository.findAll().stream()
                    .filter(fo -> fo.getTicket() != null && fo.getTicket().getId().equals(id))
                    .toList();
            if (!foodOrders.isEmpty()) {
                foodOrderRepository.deleteAll(foodOrders);
                foodOrderRepository.flush();
            }
        } catch (Exception ex) {
            System.err.println("Failed to delete food orders for ticket " + id + ": " + ex.getMessage());
        }

        // 5. Detach payment before deleting
        if (t.getPayment() != null) {
            t.setPayment(null);
            ticketRepository.save(t);
            ticketRepository.flush();
        }

        // 6. Xóa chat messages
        try {
            ticketRepository.flush();
        } catch (Exception ex) {
            System.err.println("Flush before ticket delete: " + ex.getMessage());
        }

        ticketRepository.delete(t);
        ticketRepository.flush();
    }

    @Transactional
    public void cancel(Long id) {
        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        // GIẢI PHÓNG GHẾ
        if (t.getStatus() == TicketStatus.CONFIRMED) {
            Seat seat = t.getSeat();
            if (seat != null) {
                seat.setActive(true);
                seatRepository.save(seat);
            }
        }

        t.setStatus(TicketStatus.CANCELLED);
        t.setCancelledAt(Instant.now());
        ticketRepository.save(t);
    }
}
