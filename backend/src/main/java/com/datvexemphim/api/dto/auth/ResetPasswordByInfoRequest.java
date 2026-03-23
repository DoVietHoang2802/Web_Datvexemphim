package com.datvexemphim.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordByInfoRequest {
    @NotBlank(message = "Email là bắt buộc")
    private String email;

    @NotBlank(message = "Họ tên là bắt buộc")
    private String fullName;

    @NotBlank(message = "Mật khẩu mới là bắt buộc")
    private String newPassword;
}
