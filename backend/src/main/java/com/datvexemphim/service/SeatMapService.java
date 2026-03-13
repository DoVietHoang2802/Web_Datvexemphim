package com.datvexemphim.service;

import com.datvexemphim.api.dto.publicapi.SeatMapResponse;
import com.datvexemphim.api.dto.publicapi.SeatMapSeatDto;
import com.datvexemphim.domain.entity.Showtime;
import com.datvexemphim.domain.repository.SeatRepository;
import com.datvexemphim.domain.repository.ShowtimeRepository;
import com.datvexemphim.domain.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;

@Service
public class SeatMapService {
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    public SeatMapService(ShowtimeRepository showtimeRepository, SeatRepository seatRepository, TicketRepository ticketRepository) {
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public SeatMapResponse getSeatMap(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found"));

        var room = showtime.getRoom();
        List<Long> bookedSeatIds = ticketRepository.findBookedSeatIds(showtimeId);
        var booked = new HashSet<>(bookedSeatIds);

        List<SeatMapSeatDto> seats = seatRepository.findByRoomIdOrderByRowLabelAscColNumberAsc(room.getId()).stream()
                .map(s -> new SeatMapSeatDto(s.getId(), s.getSeatCode(), s.getRowLabel(), s.getColNumber(), booked.contains(s.getId())))
                .toList();

        return new SeatMapResponse(showtimeId, room.getId(), room.getName(), room.getTotalRows(), room.getTotalCols(), seats);
    }
}

