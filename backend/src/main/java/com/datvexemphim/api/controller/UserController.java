package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.user.ChangePasswordRequest;
import com.datvexemphim.api.dto.user.UpdateProfileRequest;
import com.datvexemphim.api.dto.user.UserProfileDto;
import com.datvexemphim.service.CurrentUserService;
import com.datvexemphim.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserProfileService userProfileService;
    private final CurrentUserService currentUserService;

    public UserController(UserProfileService userProfileService, CurrentUserService currentUserService) {
        this.userProfileService = userProfileService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(@RequestBody UpdateProfileRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        userProfileService.changePassword(userId, request);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
}
