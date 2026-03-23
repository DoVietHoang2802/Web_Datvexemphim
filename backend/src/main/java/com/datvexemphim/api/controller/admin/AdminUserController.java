package com.datvexemphim.api.controller.admin;

import com.datvexemphim.api.dto.admin.AdminUserDto;
import com.datvexemphim.api.dto.admin.AdminUserUpsertRequest;
import com.datvexemphim.service.admin.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin
public class AdminUserController {
    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<AdminUserDto> list() {
        return adminUserService.list();
    }

    @GetMapping("/{id}")
    public AdminUserDto get(@PathVariable Long id) {
        return adminUserService.get(id);
    }

    @PostMapping
    public AdminUserDto create(@Valid @RequestBody AdminUserUpsertRequest req) {
        return adminUserService.create(req);
    }

    @PutMapping("/{id}")
    public AdminUserDto update(@PathVariable Long id, @Valid @RequestBody AdminUserUpsertRequest req) {
        return adminUserService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adminUserService.delete(id);
    }

    @PostMapping("/{id}/change-role")
    public AdminUserDto changeRole(@PathVariable Long id, @RequestParam String role) {
        return adminUserService.changeRole(id, role);
    }

    @PostMapping("/{id}/reset-password")
    public AdminUserDto resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        return adminUserService.resetPassword(id, newPassword);
    }
}

