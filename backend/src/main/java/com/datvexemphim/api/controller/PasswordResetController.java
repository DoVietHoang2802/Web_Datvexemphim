package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.auth.ResetPasswordByInfoRequest;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Đặt lại mật khẩu bằng email + họ tên (không cần token)
     * Áp dụng cho cả tài khoản thường và Google
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordByInfoRequest request) {
        // Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email không tồn tại trong hệ thống"
            ));
        }

        // Kiểm tra họ tên có khớp không (so sánh không phân biệt hoa thường, bỏ dấu)
        String inputName = removeDiacritics(request.getFullName().trim().toLowerCase());
        String storedName = removeDiacritics(user.getFullName().toLowerCase());

        if (!inputName.equals(storedName)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Họ tên không khớp với email trong hệ thống"
            ));
        }

        // Kiểm tra mật khẩu mới
        if (request.getNewPassword().length() < 6) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Mật khẩu mới phải có ít nhất 6 ký tự"
            ));
        }

        // Cập nhật mật khẩu
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đổi mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới."
        ));
    }

    // Hàm bỏ dấu tiếng Việt
    private String removeDiacritics(String text) {
        if (text == null) return "";
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
