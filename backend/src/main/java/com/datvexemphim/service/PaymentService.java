package com.datvexemphim.service;

import com.datvexemphim.api.dto.payment.PaymentSimulateRequest;
import com.datvexemphim.api.dto.payment.PaymentSimulateResponse;
import com.datvexemphim.domain.entity.Payment;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.enums.PaymentStatus;
import com.datvexemphim.domain.enums.TicketStatus;
import com.datvexemphim.domain.repository.PaymentRepository;
import com.datvexemphim.domain.repository.SeatRepository;
import com.datvexemphim.domain.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final FoodOrderService foodOrderService;
    private final SeatRepository seatRepository;

    public PaymentService(TicketRepository ticketRepository,
                         PaymentRepository paymentRepository,
                         FoodOrderService foodOrderService,
                         SeatRepository seatRepository) {
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.foodOrderService = foodOrderService;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public PaymentSimulateResponse simulate(PaymentSimulateRequest req, User user) {
        List<Long> ids = req.ticketIds().stream().distinct().toList();
        List<Ticket> tickets = ticketRepository.findByIdIn(ids);
        if (tickets.size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket không hợp lệ.");
        }
        boolean anyNotOwned = tickets.stream().anyMatch(t -> !t.getOwner().getId().equals(user.getId()));
        if (anyNotOwned) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thanh toán ticket này.");
        }
        boolean anyNotPending = tickets.stream().anyMatch(t -> t.getStatus() != TicketStatus.PENDING);
        if (anyNotPending) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ ticket PENDING mới được thanh toán.");
        }

        // Calculate ticket total (dùng ticket.price vì vé tặng = 0)
        long ticketAmount = tickets.stream().mapToLong(t -> t.getPrice() != null ? t.getPrice() : 0).sum();

        String bookingCode = "BK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        if (Boolean.TRUE.equals(req.success())) {
            // TẠO PAYMENT TRƯỚC ĐỂ FOOD ORDER CÓ THỂ THAM CHIẾU
            Payment payment = new Payment();
            payment.setUser(user);
            payment.setBookingCode(bookingCode);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(Instant.now());
            payment.setAmount(ticketAmount); // Tạm đặt = ticket, food order sẽ cập nhật sau
            paymentRepository.save(payment);

            // Cập nhật tickets với payment
            for (Ticket t : tickets) {
                t.setStatus(TicketStatus.CONFIRMED);
                t.setPayment(payment);
                // ĐÁNH DẤU GHẾ LÀ ĐÃ ĐẶT
                if (t.getSeat() != null) {
                    t.getSeat().setActive(false);
                    seatRepository.save(t.getSeat());
                }
                // Sinh mã vé QR nếu chưa có
                if (t.getTicketCode() == null || t.getTicketCode().isBlank()) {
                    String ticketCode = "VE" + System.currentTimeMillis() + String.format("%03d", t.getId());
                    t.setTicketCode(ticketCode);
                }
            }
            ticketRepository.saveAll(tickets);

            return new PaymentSimulateResponse(payment.getId(), bookingCode, payment.getStatus().name(), ticketAmount);
        }

        // Fail: record failed payment and release seats by deleting pending tickets
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setBookingCode(bookingCode);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setAmount(ticketAmount);
        paymentRepository.save(payment);

        // GIẢI PHÓNG GHẾ KHI HỦY PENDING TICKETS
        for (Ticket t : tickets) {
            if (t.getSeat() != null) {
                t.getSeat().setActive(true);
                seatRepository.save(t.getSeat());
            }
        }

        ticketRepository.deleteAll(tickets);
        return new PaymentSimulateResponse(payment.getId(), bookingCode, payment.getStatus().name(), ticketAmount);
    }
}

