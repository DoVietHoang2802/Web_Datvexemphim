package com.datvexemphim.service;

import com.datvexemphim.config.VnpayConfig;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnpayService {

    private static final Logger log = LoggerFactory.getLogger(VnpayService.class);
    private final VnpayConfig vnpayConfig;

    public VnpayService(VnpayConfig vnpayConfig) {
        this.vnpayConfig = vnpayConfig;
    }

    /**
     * Tạo URL thanh toán VNPay
     */
    public String createPaymentUrl(long amount, String orderId, String orderInfo) {
        try {
            Map<String, String> vnp_Params = new LinkedHashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_BankCode", "");
            vnp_Params.put("vnp_TxnRef", orderId);
            vnp_Params.put("vnp_OrderInfo", orderInfo);
            vnp_Params.put("vnp_OrderType", "250000");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
            vnp_Params.put("vnp_IpAddr", "127.0.0.1");
            vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

            // Sắp xếp params theo key
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            // Tạo hashData (không encode)
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String fieldName : fieldNames) {
                String value = vnp_Params.get(fieldName);
                if (hashData.length() > 0) {
                    hashData.append('&');
                    query.append('&');
                }
                hashData.append(fieldName).append('=').append(value);
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                     .append('=')
                     .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }

            log.info("VNPay HashSecret: {}", vnpayConfig.getHashSecret());
            log.info("VNPay TmnCode: {}", vnpayConfig.getTmnCode());
            log.info("VNPay HashData: {}", hashData.toString());

            // Tạo secure hash với SHA512
            String vnp_SecureHash = hmacSHA512(vnpayConfig.getHashSecret(), hashData.toString());
            log.info("VNPay SecureHash: {}", vnp_SecureHash);

            String queryUrl = query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = vnpayConfig.getApiUrl() + "?" + queryUrl;

            log.info("VNPay PaymentUrl: {}", paymentUrl);

            return paymentUrl;

        } catch (Exception e) {
            log.error("Lỗi tạo URL thanh toán", e);
            throw new RuntimeException("Lỗi tạo URL thanh toán: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra chữ ký từ VNPay return
     */
    public boolean verifyReturn(HashMap<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if (hashData.length() > 0) {
                hashData.append('&');
            }
            hashData.append(fieldName).append('=').append(value);
        }

        String mySecureHash = hmacSHA512(vnpayConfig.getHashSecret(), hashData.toString());
        return mySecureHash.equalsIgnoreCase(vnp_SecureHash);
    }

    public String generateOrderId() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +
               String.format("%04d", new Random().nextInt(10000));
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo secure hash", e);
        }
    }
}
