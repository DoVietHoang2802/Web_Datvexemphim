package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.booking.ApplyVoucherRequest;
import com.datvexemphim.domain.entity.Voucher;
import com.datvexemphim.service.VoucherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {
    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestBody ApplyVoucherRequest request) {
        try {
            Voucher voucher = voucherService.applyVoucher(request.getVoucherCode(), request.getOrderAmount());
            Long discount = voucherService.calculateDiscount(voucher, request.getOrderAmount());

            return ResponseEntity.ok(Map.of(
                "valid", true,
                "code", voucher.getCode(),
                "description", voucher.getDescription(),
                "discountPercent", voucher.getDiscountPercent(),
                "maxDiscount", voucher.getMaxDiscount(),
                "discountAmount", discount,
                "message", "Áp dụng thành công!"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "message", e.getMessage()
            ));
        }
    }
}
