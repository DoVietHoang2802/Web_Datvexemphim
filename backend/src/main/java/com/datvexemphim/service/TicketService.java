package com.datvexemphim.service;

import com.datvexemphim.api.dto.ticket.TicketHistoryDto;
import com.datvexemphim.api.dto.ticket.TransferTicketRequest;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.entity.TransferHistory;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.entity.Seat;
import com.datvexemphim.domain.enums.TicketStatus;
import com.datvexemphim.domain.repository.TicketRepository;
import com.datvexemphim.domain.repository.TransferHistoryRepository;
import com.datvexemphim.domain.repository.UserRepository;
import com.datvexemphim.domain.repository.SeatRepository;
import com.datvexemphim.domain.repository.FoodOrderRepository;

import java.math.BigDecimal;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TransferHistoryRepository transferHistoryRepository;
    private final FoodOrderService foodOrderService;
    private final SeatRepository seatRepository;
    private final FoodOrderRepository foodOrderRepository;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         TransferHistoryRepository transferHistoryRepository,
                         FoodOrderService foodOrderService,
                         SeatRepository seatRepository,
                         FoodOrderRepository foodOrderRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.transferHistoryRepository = transferHistoryRepository;
        this.foodOrderService = foodOrderService;
        this.seatRepository = seatRepository;
        this.foodOrderRepository = foodOrderRepository;
    }

    /**
     * Map Ticket entity -> TicketHistoryDto, kèm food order details.
     */
    private TicketHistoryDto toTicketHistoryDto(Ticket t) {
        List<TicketHistoryDto.FoodOrderItemSummary> foodItems = List.of();
        Long foodOrderId = null;
        BigDecimal foodTotalPrice = null;
        String foodOrderStatus = null;

        try {
            var foodOrderOpt = foodOrderRepository.findByTicket(t);
            if (foodOrderOpt.isPresent()) {
                var foodOrder = foodOrderOpt.get();
                foodOrderId = foodOrder.getId();
                foodTotalPrice = foodOrder.getTotalPrice();
                foodOrderStatus = foodOrder.getFoodOrderStatus().name();
                if (foodOrder.getItems() != null) {
                    foodItems = foodOrder.getItems().stream()
                            .map(item -> new TicketHistoryDto.FoodOrderItemSummary(
                                    item.getFoodItem().getName(),
                                    item.getQuantity(),
                                    item.getPriceAtOrder(),
                                    item.getSubtotal()
                            ))
                            .toList();
                }
            }
        } catch (Exception e) {
            // If food order fetch fails, just continue without food info
        }

        return new TicketHistoryDto(
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
                t.getPayment() == null ? null : t.getPayment().getBookingCode(),
                foodOrderId,
                foodTotalPrice,
                foodOrderStatus,
                foodItems
        );
    }

    @Transactional
    public List<TicketHistoryDto> getByIds(List<Long> ids, User user) {
        return ticketRepository.findByIdIn(ids).stream()
                .filter(t -> t.getOwner().getId().equals(user.getId()))
                .map(this::toTicketHistoryDto)
                .toList();
    }

    @Transactional
    public List<TicketHistoryDto> myTickets(User user) {
        return ticketRepository.findByOwnerIdOrderByBookedAtDesc(user.getId()).stream()
                .map(this::toTicketHistoryDto)
                .toList();
    }

    @Transactional
    public void cancel(Long ticketId, User user) {
        Ticket t = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        if (!t.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền.");
        }
        if (t.getStatus() != TicketStatus.CONFIRMED && t.getStatus() != TicketStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vé đã bị hủy trước đó rồi.");
        }

        // ===== GIẢI PHÓNG GHẾ (nếu là vé CONFIRMED) =====
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

    @Transactional
    public void delete(Long ticketId, User user) {
        Ticket t = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        if (!t.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền.");
        }
        if (t.getStatus() != TicketStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ vé đã hủy mới được xóa.");
        }
        ticketRepository.delete(t);
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

        foodOrderService.handleTicketTransfer(t.getId(), toUser.getId(), savedTransferHistory.getId());
    }
}
