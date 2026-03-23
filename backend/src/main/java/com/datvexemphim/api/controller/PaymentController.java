package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.payment.PaymentSimulateRequest;
import com.datvexemphim.api.dto.payment.PaymentSimulateResponse;
import com.datvexemphim.service.CurrentUserService;
import com.datvexemphim.service.PaymentService;
import com.datvexemphim.service.VnpayService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {
    private final PaymentService paymentService;
    private final CurrentUserService currentUserService;
    private final VnpayService vnpayService;

    public PaymentController(PaymentService paymentService, CurrentUserService currentUserService, VnpayService vnpayService) {
        this.paymentService = paymentService;
        this.currentUserService = currentUserService;
        this.vnpayService = vnpayService;
    }

    @PostMapping("/simulate")
    public PaymentSimulateResponse simulate(@Valid @RequestBody PaymentSimulateRequest req) {
        return paymentService.simulate(req, currentUserService.requireUser());
    }

    /**
     * Tạo URL thanh toán VNPay
     */
    @PostMapping("/vnpay/create")
    public Map<String, String> createVnpayPayment(@RequestBody Map<String, Object> request) {
        long amount = Long.parseLong(request.get("amount").toString());
        String ticketIdsStr = request.get("ticketIds").toString();
        String orderInfo = "Thanh toan ve xem phim - Tickets: " + ticketIdsStr;
        String orderId = vnpayService.generateOrderId();

        String paymentUrl = vnpayService.createPaymentUrl(amount, orderId, orderInfo);

        return Map.of(
            "paymentUrl", paymentUrl,
            "orderId", orderId
        );
    }
}

