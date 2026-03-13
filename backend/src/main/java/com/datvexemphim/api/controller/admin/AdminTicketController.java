package com.datvexemphim.api.controller.admin;

import com.datvexemphim.api.dto.admin.AdminTicketDto;
import com.datvexemphim.api.dto.admin.AdminTicketUpsertRequest;
import com.datvexemphim.service.admin.AdminTicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tickets")
@CrossOrigin
public class AdminTicketController {
    private final AdminTicketService adminTicketService;

    public AdminTicketController(AdminTicketService adminTicketService) {
        this.adminTicketService = adminTicketService;
    }

    @GetMapping
    public List<AdminTicketDto> list() {
        return adminTicketService.list();
    }

    @GetMapping("/{id}")
    public AdminTicketDto get(@PathVariable Long id) {
        return adminTicketService.get(id);
    }

    @PostMapping
    public AdminTicketDto create(@Valid @RequestBody AdminTicketUpsertRequest req) {
        return adminTicketService.create(req);
    }

    @PutMapping("/{id}")
    public AdminTicketDto update(@PathVariable Long id, @Valid @RequestBody AdminTicketUpsertRequest req) {
        return adminTicketService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adminTicketService.delete(id);
    }

    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        adminTicketService.cancel(id);
    }
}

