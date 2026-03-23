package com.datvexemphim.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * Upload image - chuyển thành Base64 data URL
     * Không cần lưu file trên server
     */
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "File rỗng"
            ));
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Định dạng không hợp lệ. Chỉ chấp nhận JPEG, PNG, GIF, WEBP."
            ));
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "File quá lớn. Kích thước tối đa là 5MB."
            ));
        }

        try {
            // Chuyển file thành Base64
            String base64Data = Base64.getEncoder().encodeToString(file.getBytes());

            // Tạo data URL (có thể dùng trực tiếp trong <img src="...">)
            String dataUrl = "data:" + contentType + ";base64," + base64Data;

            return ResponseEntity.ok(Map.of(
                "url", dataUrl,
                "type", contentType,
                "size", file.getSize()
            ));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Upload thất bại: " + e.getMessage()
            ));
        }
    }
}
