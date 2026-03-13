package com.datvexemphim.service;

import com.datvexemphim.api.dto.payment.PaymentSimulateRequest;
import com.datvexemphim.api.dto.payment.PaymentSimulateResponse;
import com.datvexemphim.domain.entity.Payment;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.enums.PaymentStatus;
import com.datvexemphim.domain.enums.TicketStatus;
import com.datvexemphim.domain.repository.PaymentRepository;
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

    public PaymentService(TicketRepository ticketRepository, 
                         PaymentRepository paymentRepository,
                         FoodOrderService foodOrderService) {
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.foodOrderService = foodOrderService;
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

        // Calculate ticket total
        long ticketAmount = tickets.stream().mapToLong(t -> t.getShowtime().getPrice()).sum();
        
        String bookingCode = "BK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setBookingCode(bookingCode);

        if (Boolean.TRUE.equals(req.success())) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(Instant.now());
            
            // Calculate total FIRST: ticket amount + any food order amounts
            long foodOrderAmount = 0L;
            if (Boolean.TRUE.equals(payment.getHasFoodOrder()) && payment.getFoodOrderTotal() != null) {
                foodOrderAmount = payment.getFoodOrderTotal().longValue();
            }
            long totalAmount = ticketAmount + foodOrderAmount;
            payment.setAmount(totalAmount);  // SET AMOUNT BEFORE SAVING!
            
            paymentRepository.save(payment);

            for (Ticket t : tickets) {
                t.setStatus(TicketStatus.CONFIRMED);
                t.setPayment(payment);
            }
            ticketRepository.saveAll(tickets);
            
            // ===== IMPORTANT: Confirm food orders if any exist =====
            // When payment succeeds, update all food orders linked to this payment
            foodOrderService.confirmFoodOrder(payment.getId());
            
            return new PaymentSimulateResponse(payment.getId(), bookingCode, payment.getStatus().name(), totalAmount);
        }

        // Fail: record failed payment and release seats by deleting pending tickets
        payment.setStatus(PaymentStatus.FAILED);
        payment.setAmount(ticketAmount);
        paymentRepository.save(payment);
        ticketRepository.deleteAll(tickets);
        return new PaymentSimulateResponse(payment.getId(), bookingCode, payment.getStatus().name(), ticketAmount);
    }
}

