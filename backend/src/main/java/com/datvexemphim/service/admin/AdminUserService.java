package com.datvexemphim.service.admin;

import com.datvexemphim.api.dto.admin.AdminUserDto;
import com.datvexemphim.api.dto.admin.AdminUserUpsertRequest;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.enums.Role;
import com.datvexemphim.domain.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AdminUserDto> list() {
        return userRepository.findAll().stream()
                .map(u -> new AdminUserDto(u.getId(), u.getFullName(), u.getEmail(), u.getRole().name(), u.getCreatedAt()))
                .toList();
    }

    public AdminUserDto get(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new AdminUserDto(u.getId(), u.getFullName(), u.getEmail(), u.getRole().name(), u.getCreatedAt());
    }

    public AdminUserDto create(AdminUserUpsertRequest req) {
        if (userRepository.existsByEmail(req.email().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã tồn tại.");
        }
        if (req.password() == null || req.password().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password tối thiểu 6 ký tự.");
        }
        User u = new User();
        u.setFullName(req.fullName());
        u.setEmail(req.email().toLowerCase());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRole(Role.valueOf(req.role()));
        User saved = userRepository.save(u);
        return new AdminUserDto(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole().name(), saved.getCreatedAt());
    }

    public AdminUserDto update(Long id, AdminUserUpsertRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        u.setFullName(req.fullName());
        u.setEmail(req.email().toLowerCase());
        u.setRole(Role.valueOf(req.role()));
        if (req.password() != null && !req.password().isBlank()) {
            if (req.password().length() < 6) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password tối thiểu 6 ký tự.");
            }
            u.setPasswordHash(passwordEncoder.encode(req.password()));
        }
        User saved = userRepository.save(u);
        return new AdminUserDto(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole().name(), saved.getCreatedAt());
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public AdminUserDto changeRole(Long id, String newRole) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        u.setRole(Role.valueOf(newRole));
        User saved = userRepository.save(u);
        return new AdminUserDto(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole().name(), saved.getCreatedAt());
    }

    public AdminUserDto resetPassword(Long id, String newPassword) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (newPassword == null || newPassword.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password tối thiểu 6 ký tự.");
        }
        u.setPasswordHash(passwordEncoder.encode(newPassword));
        User saved = userRepository.save(u);
        return new AdminUserDto(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole().name(), saved.getCreatedAt());
    }
}

