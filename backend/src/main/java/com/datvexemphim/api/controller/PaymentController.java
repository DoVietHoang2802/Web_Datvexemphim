package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.payment.PaymentSimulateRequest;
import com.datvexemphim.api.dto.payment.PaymentSimulateResponse;
import com.datvexemphim.service.CurrentUserService;
import com.datvexemphim.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {
    private final PaymentService paymentService;
    private final CurrentUserService currentUserService;

    public PaymentController(PaymentService paymentService, CurrentUserService currentUserService) {
        this.paymentService = paymentService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/simulate")
    public PaymentSimulateResponse simulate(@Valid @RequestBody PaymentSimulateRequest req) {
        return paymentService.simulate(req, currentUserService.requireUser());
    }
}

