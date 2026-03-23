package com.datvexemphim.api.controller.admin;

import com.datvexemphim.api.dto.admin.AdminSeatDto;
import com.datvexemphim.api.dto.admin.SeatUpsertRequest;
import com.datvexemphim.service.admin.AdminSeatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/seats")
@CrossOrigin
public class AdminSeatController {
    private final AdminSeatService adminSeatService;

    public AdminSeatController(AdminSeatService adminSeatService) {
        this.adminSeatService = adminSeatService;
    }

    @GetMapping("/room/{roomId}")
    public List<AdminSeatDto> listByRoom(@PathVariable Long roomId) {
        return adminSeatService.listByRoom(roomId);
    }

    @GetMapping("/{id}")
    public AdminSeatDto get(@PathVariable Long id) {
        return adminSeatService.get(id);
    }

    @PostMapping
    public AdminSeatDto create(@Valid @RequestBody SeatUpsertRequest req) {
        return adminSeatService.create(req);
    }

    @PutMapping("/{id}")
    public AdminSeatDto update(@PathVariable Long id, @Valid @RequestBody SeatUpsertRequest req) {
        return adminSeatService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adminSeatService.delete(id);
    }
}

