package com.datvexemphim.service.admin;

import com.datvexemphim.api.dto.admin.GenerateSeatsRequest;
import com.datvexemphim.api.dto.admin.RoomUpsertRequest;
import com.datvexemphim.domain.entity.Room;
import com.datvexemphim.domain.entity.Seat;
import com.datvexemphim.domain.repository.RoomRepository;
import com.datvexemphim.domain.repository.SeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminRoomService {
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;

    public AdminRoomService(RoomRepository roomRepository, SeatRepository seatRepository) {
        this.roomRepository = roomRepository;
        this.seatRepository = seatRepository;
    }

    public List<Room> list() {
        return roomRepository.findAll();
    }

    public Room get(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
    }

    public Room create(RoomUpsertRequest req) {
        Room r = new Room();
        r.setName(req.name());
        r.setTotalRows(req.totalRows());
        r.setTotalCols(req.totalCols());
        return roomRepository.save(r);
    }

    public Room update(Long id, RoomUpsertRequest req) {
        Room r = get(id);
        r.setName(req.name());
        r.setTotalRows(req.totalRows());
        r.setTotalCols(req.totalCols());
        return roomRepository.save(r);
    }

    public void delete(Long id) {
        roomRepository.deleteById(id);
    }

    @Transactional
    public void generateSeats(GenerateSeatsRequest req) {
        Room room = get(req.roomId());
        room.setTotalRows(req.totalRows());
        room.setTotalCols(req.totalCols());
        roomRepository.save(room);

        // Delete existing seats first to allow regeneration
        List<Seat> existingSeats = seatRepository.findByRoomIdOrderByRowLabelAscColNumberAsc(room.getId());
        if (!existingSeats.isEmpty()) {
            seatRepository.deleteAll(existingSeats);
        }

        List<Seat> seats = new ArrayList<>();
        for (int r = 0; r < room.getTotalRows(); r++) {
            String rowLabel = String.valueOf((char) ('A' + r));
            for (int c = 1; c <= room.getTotalCols(); c++) {
                Seat s = new Seat();
                s.setRoom(room);
                s.setRowLabel(rowLabel);
                s.setColNumber(c);
                s.setSeatCode(rowLabel + c);
                seats.add(s);
            }
        }
        seatRepository.saveAll(seats);
    }
}

