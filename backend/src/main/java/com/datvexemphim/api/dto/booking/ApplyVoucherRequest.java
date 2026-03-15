package com.datvexemphim.api.dto.booking;

public class ApplyVoucherRequest {
    private String voucherCode;
    private Long orderAmount;

    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public Long getOrderAmount() { return orderAmount; }
    public void setOrderAmount(Long orderAmount) { this.orderAmount = orderAmount; }
}
