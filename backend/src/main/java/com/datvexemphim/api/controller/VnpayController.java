package com.datvexemphim.api.controller;

import com.datvexemphim.domain.entity.Payment;
import com.datvexemphim.domain.enums.PaymentStatus;
import com.datvexemphim.domain.repository.PaymentRepository;
import com.datvexemphim.domain.repository.UserRepository;
import com.datvexemphim.service.VnpayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
@CrossOrigin(origins = "*")
public class VnpayController {

    private final VnpayService vnpayService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public VnpayController(VnpayService vnpayService, PaymentRepository paymentRepository, UserRepository userRepository) {
        this.vnpayService = vnpayService;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Tạo URL thanh toán VNPay
     */
    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> request) {
        try {
            String orderId = vnpayService.generateOrderId();
            long amount = Long.parseLong(request.get("amount").toString());
            String orderInfo = request.getOrDefault("orderInfo", "Thanh toan ve xem phim").toString();
            Long userId = request.containsKey("userId") ?
                Long.parseLong(request.get("userId").toString()) : null;

            // Tạo payment record
            Payment payment = new Payment();
            payment.setAmount(amount);
            payment.setProvider("VNPAY");
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTransactionId(orderId);
            payment.setBookingCode(orderId);
            if (userId != null) {
                userRepository.findById(userId).ifPresent(payment::setUser);
            }
            paymentRepository.save(payment);

            // Tạo URL thanh toán
            String paymentUrl = vnpayService.createPaymentUrl(amount, orderId, orderInfo);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "paymentUrl", paymentUrl,
                "orderId", orderId
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi tạo thanh toán: " + e.getMessage()
            ));
        }
    }

    /**
     * Xử lý return từ VNPay
     */
    @GetMapping("/return")
    public ResponseEntity<?> vnpayReturn(
            @RequestParam Map<String, String> params) {
        try {
            String responseCode = params.get("vnp_ResponseCode");
            String transactionId = params.get("vnp_TxnRef");

            // Tìm payment
            paymentRepository.findByTransactionId(transactionId).ifPresent(payment -> {
                if ("00".equals(responseCode)) {
                    payment.setStatus(PaymentStatus.SUCCESS);
                } else {
                    payment.setStatus(PaymentStatus.FAILED);
                }
                paymentRepository.save(payment);
            });

            if ("00".equals(responseCode)) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thanh toán thành công!",
                    "transactionId", transactionId
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Thanh toán thất bại. Mã lỗi: " + responseCode,
                    "transactionId", transactionId
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi xử lý: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy cấu hình VNPay (TMN Code để test)
     */
    @GetMapping("/config")
    public ResponseEntity<?> getConfig() {
        return ResponseEntity.ok(Map.of(
            "vnpayUrl", "https://sandbox.vnpayment.vn/apis/vnpay/effect",
            "returnUrl", "https://web-datvexemphim.vercel.app/checkout.html"
        ));
    }
}
