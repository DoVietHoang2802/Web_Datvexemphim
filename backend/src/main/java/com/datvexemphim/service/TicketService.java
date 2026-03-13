package com.datvexemphim.service;

import com.datvexemphim.api.dto.ticket.TicketHistoryDto;
import com.datvexemphim.api.dto.ticket.TransferTicketRequest;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.entity.TransferHistory;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.enums.TicketStatus;
import com.datvexemphim.domain.repository.TicketRepository;
import com.datvexemphim.domain.repository.TransferHistoryRepository;
import com.datvexemphim.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class TicketService {
    private static final Duration CANCEL_BEFORE = Duration.ofMinutes(30);

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TransferHistoryRepository transferHistoryRepository;
    private final FoodOrderService foodOrderService;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         TransferHistoryRepository transferHistoryRepository,
                         FoodOrderService foodOrderService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.transferHistoryRepository = transferHistoryRepository;
        this.foodOrderService = foodOrderService;
    }

    @Transactional
    public List<TicketHistoryDto> myTickets(User user) {
        return ticketRepository.findByOwnerIdOrderByBookedAtDesc(user.getId()).stream()
                .map(t -> new TicketHistoryDto(
                        t.getId(),
                        t.getStatus().name(),
                        t.getBookedAt(),
                        t.getCancelledAt(),
                        t.getShowtime().getId(),
                        t.getShowtime().getStartTime(),
                        t.getShowtime().getMovie().getTitle(),
                        t.getShowtime().getRoom().getName(),
                        t.getSeat().getSeatCode(),
                        t.getShowtime().getPrice(),
                        t.getPayment() == null ? null : t.getPayment().getBookingCode()
                ))
                .toList();
    }

    @Transactional
    public void cancel(Long ticketId, User user) {
        Ticket t = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        if (!t.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền.");
        }
        if (t.getStatus() != TicketStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ ticket CONFIRMED mới được hủy.");
        }

        Instant now = Instant.now();
        Instant start = t.getShowtime().getStartTime();
        if (!now.isBefore(start.minus(CANCEL_BEFORE))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được hủy trước giờ chiếu 30 phút.");
        }

        t.setStatus(TicketStatus.CANCELLED);
        t.setCancelledAt(now);
        ticketRepository.save(t);
    }

    @Transactional
    public void transfer(TransferTicketRequest req, User fromUser) {
        Ticket t = ticketRepository.findById(req.ticketId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        if (!t.getOwner().getId().equals(fromUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền.");
        }
        if (t.getStatus() != TicketStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ ticket CONFIRMED mới được chuyển.");
        }
        if (!t.getShowtime().getStartTime().isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Suất chiếu đã bắt đầu/đã qua.");
        }

        User toUser = userRepository.findByEmail(req.toEmail().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User nhận vé không tồn tại."));

        if (toUser.getId().equals(fromUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể chuyển cho chính mình.");
        }

        t.setOwner(toUser);
        ticketRepository.save(t);

        TransferHistory th = new TransferHistory();
        th.setTicket(t);
        th.setFromUser(fromUser);
        th.setToUser(toUser);
        TransferHistory savedTransferHistory = transferHistoryRepository.save(th);
        
        // ===== IMPORTANT: Update food order owner if exists =====
        // When ticket is transferred, update food order's current owner
        // but keep original buyer unchanged (for refund/tracking purposes)
        foodOrderService.handleTicketTransfer(t.getId(), toUser.getId(), savedTransferHistory.getId());
    }
}

