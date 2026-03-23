package com.datvexemphim.service.admin;

import com.datvexemphim.api.dto.admin.AdminSeatDto;
import com.datvexemphim.api.dto.admin.SeatUpsertRequest;
import com.datvexemphim.domain.entity.Seat;
import com.datvexemphim.domain.entity.Room;
import com.datvexemphim.domain.repository.RoomRepository;
import com.datvexemphim.domain.repository.SeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminSeatService {
    private final SeatRepository seatRepository;
    private final RoomRepository roomRepository;

    public AdminSeatService(SeatRepository seatRepository, RoomRepository roomRepository) {
        this.seatRepository = seatRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public List<AdminSeatDto> listByRoom(Long roomId) {
        return seatRepository.findByRoomIdOrderByRowLabelAscColNumberAsc(roomId).stream()
                .map(s -> new AdminSeatDto(s.getId(), s.getRoom().getId(), s.getSeatCode(), s.getRowLabel(), s.getColNumber()))
                .toList();
    }

    @Transactional
    public AdminSeatDto get(Long id) {
        Seat s = seatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
        return new AdminSeatDto(s.getId(), s.getRoom().getId(), s.getSeatCode(), s.getRowLabel(), s.getColNumber());
    }

    @Transactional
    public AdminSeatDto create(SeatUpsertRequest req) {
        Room room = roomRepository.findById(req.roomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found"));
        Seat s = new Seat();
        s.setRoom(room);
        s.setRowLabel(req.rowLabel());
        s.setColNumber(req.colNumber());
        s.setSeatCode(req.seatCode());
        Seat saved = seatRepository.save(s);
        return new AdminSeatDto(saved.getId(), saved.getRoom().getId(), saved.getSeatCode(), saved.getRowLabel(), saved.getColNumber());
    }

    @Transactional
    public AdminSeatDto update(Long id, SeatUpsertRequest req) {
        Seat s = seatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
        Room room = roomRepository.findById(req.roomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found"));
        s.setRoom(room);
        s.setRowLabel(req.rowLabel());
        s.setColNumber(req.colNumber());
        s.setSeatCode(req.seatCode());
        Seat saved = seatRepository.save(s);
        return new AdminSeatDto(saved.getId(), saved.getRoom().getId(), saved.getSeatCode(), saved.getRowLabel(), saved.getColNumber());
    }

    public void delete(Long id) {
        seatRepository.deleteById(id);
    }
}

