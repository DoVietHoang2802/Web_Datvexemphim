package com.datvexemphim.api.controller.admin;

import com.datvexemphim.api.dto.admin.GenerateSeatsRequest;
import com.datvexemphim.api.dto.admin.RoomUpsertRequest;
import com.datvexemphim.domain.entity.Room;
import com.datvexemphim.service.admin.AdminRoomService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rooms")
@CrossOrigin
public class AdminRoomController {
    private final AdminRoomService adminRoomService;

    public AdminRoomController(AdminRoomService adminRoomService) {
        this.adminRoomService = adminRoomService;
    }

    @GetMapping
    public List<Room> list() {
        return adminRoomService.list();
    }

    @GetMapping("/{id}")
    public Room get(@PathVariable Long id) {
        return adminRoomService.get(id);
    }

    @PostMapping
    public Room create(@Valid @RequestBody RoomUpsertRequest req) {
        return adminRoomService.create(req);
    }

    @PutMapping("/{id}")
    public Room update(@PathVariable Long id, @Valid @RequestBody RoomUpsertRequest req) {
        return adminRoomService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adminRoomService.delete(id);
    }

    @PostMapping("/generate-seats")
    public void generateSeats(@Valid @RequestBody GenerateSeatsRequest req) {
        adminRoomService.generateSeats(req);
    }
}

