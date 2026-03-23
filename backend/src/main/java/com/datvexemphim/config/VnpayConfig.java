package com.datvexemphim.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VnpayConfig {

    @Value("${VNPAY_URL:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpayUrl;

    @Value("${VNPAY_RETURN_URL:https://web-datvexemphim.vercel.app/checkout.html}")
    private String returnUrl;

    @Value("${VNPAY_TMN_CODE:}")
    private String tmnCode;

    @Value("${VNPAY_HASH_SECRET:}")
    private String hashSecret;

    @Value("${VNPAY_API_URL:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String apiUrl;

    public String getVnpayUrl() {
        return vnpayUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getTmnCode() {
        return tmnCode;
    }

    public String getHashSecret() {
        return hashSecret;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}
