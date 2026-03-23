package com.datvexemphim.service;

import com.datvexemphim.api.dto.auth.AuthResponse;
import com.datvexemphim.api.dto.auth.GoogleLoginRequest;
import com.datvexemphim.api.dto.auth.LoginRequest;
import com.datvexemphim.api.dto.auth.RegisterRequest;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.enums.AuthProvider;
import com.datvexemphim.domain.enums.Role;
import com.datvexemphim.domain.repository.UserRepository;
import com.datvexemphim.security.JwtService;
import com.datvexemphim.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã tồn tại.");
        }
        User user = new User();
        user.setFullName(req.fullName());
        user.setEmail(req.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(Role.USER);
        userRepository.save(user);

        String token = jwtService.generateAccessToken(user.getEmail(), Map.of("role", user.getRole().name()));
        return AuthResponse.bearer(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email().toLowerCase(), req.password())
        );
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = principal.getUser();
        String token = jwtService.generateAccessToken(user.getEmail(), Map.of("role", user.getRole().name()));
        return AuthResponse.bearer(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse googleLogin(GoogleLoginRequest req) {
        String email = req.getEmail().toLowerCase();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Tạo user mới từ Google
            user = new User();
            user.setFullName(req.getDisplayName() != null ? req.getDisplayName() : "Google User");
            user.setEmail(email);
            // Đặt mật khẩu ngẫu nhiên (không dùng được vì đăng nhập bằng Google)
            user.setPasswordHash(passwordEncoder.encode("GOOGLE_" + System.currentTimeMillis()));
            user.setRole(Role.USER);
            user.setProvider(AuthProvider.GOOGLE);
            userRepository.save(user);
        }

        String token = jwtService.generateAccessToken(user.getEmail(), Map.of("role", user.getRole().name()));
        return AuthResponse.bearer(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }
}

