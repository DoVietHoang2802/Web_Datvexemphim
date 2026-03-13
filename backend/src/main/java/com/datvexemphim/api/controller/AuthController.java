package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.auth.AuthResponse;
import com.datvexemphim.api.dto.auth.LoginRequest;
import com.datvexemphim.api.dto.auth.RegisterRequest;
import com.datvexemphim.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}

