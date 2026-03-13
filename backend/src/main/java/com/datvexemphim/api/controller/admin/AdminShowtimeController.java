package com.datvexemphim.api.controller.admin;

import com.datvexemphim.api.dto.admin.AdminShowtimeDto;
import com.datvexemphim.api.dto.admin.ShowtimeUpsertRequest;
import com.datvexemphim.domain.entity.Showtime;
import com.datvexemphim.service.admin.AdminShowtimeService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/showtimes")
@CrossOrigin
public class AdminShowtimeController {
    private final AdminShowtimeService adminShowtimeService;

    public AdminShowtimeController(AdminShowtimeService adminShowtimeService) {
        this.adminShowtimeService = adminShowtimeService;
    }

    @GetMapping
    public List<AdminShowtimeDto> list() {
        return adminShowtimeService.list();
    }

    @Transactional
    @GetMapping("/{id}")
    public AdminShowtimeDto get(@PathVariable Long id) {
        var s = adminShowtimeService.get(id);
        return new AdminShowtimeDto(
                s.getId(),
                s.getMovie().getId(),
                s.getMovie().getTitle(),
                s.getRoom().getId(),
                s.getRoom().getName(),
                s.getStartTime(),
                s.getEndTime(),
                s.getPrice()
        );
    }

    @PostMapping
    public AdminShowtimeDto create(@Valid @RequestBody ShowtimeUpsertRequest req) {
        var s = adminShowtimeService.create(req);
        return new AdminShowtimeDto(
                s.getId(),
                s.getMovie().getId(),
                s.getMovie().getTitle(),
                s.getRoom().getId(),
                s.getRoom().getName(),
                s.getStartTime(),
                s.getEndTime(),
                s.getPrice()
        );
    }

    @PutMapping("/{id}")
    public AdminShowtimeDto update(@PathVariable Long id, @Valid @RequestBody ShowtimeUpsertRequest req) {
        var s = adminShowtimeService.update(id, req);
        return new AdminShowtimeDto(
                s.getId(),
                s.getMovie().getId(),
                s.getMovie().getTitle(),
                s.getRoom().getId(),
                s.getRoom().getName(),
                s.getStartTime(),
                s.getEndTime(),
                s.getPrice()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adminShowtimeService.delete(id);
    }

    @PostMapping("/{id}/end")
    public void end(@PathVariable Long id) {
        adminShowtimeService.end(id);
    }
}

