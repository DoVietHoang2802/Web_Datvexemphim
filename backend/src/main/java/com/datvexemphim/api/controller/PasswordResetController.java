package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.auth.ResetPasswordByInfoRequest;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordByInfoRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email không tồn tại trong hệ thống"
            ));
        }

        String inputName = removeDiacritics(request.getFullName().trim().toLowerCase());
        String storedName = removeDiacritics(user.getFullName().toLowerCase());

        if (!inputName.equals(storedName)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Họ tên không khớp với email trong hệ thống"
            ));
        }

        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Mật khẩu mới phải có ít nhất 6 ký tự"
            ));
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đổi mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới."
        ));
    }

    private String removeDiacritics(String text) {
        if (text == null) return "";
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
