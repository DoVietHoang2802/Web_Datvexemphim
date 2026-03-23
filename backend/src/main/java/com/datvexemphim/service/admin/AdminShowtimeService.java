package com.datvexemphim.service.admin;

import com.datvexemphim.api.dto.admin.AdminShowtimeDto;
import com.datvexemphim.api.dto.admin.ShowtimeUpsertRequest;
import com.datvexemphim.domain.entity.Movie;
import com.datvexemphim.domain.entity.Room;
import com.datvexemphim.domain.entity.Showtime;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.enums.ShowtimeStatus;
import com.datvexemphim.domain.enums.TicketStatus;
import com.datvexemphim.domain.repository.MovieRepository;
import com.datvexemphim.domain.repository.RoomRepository;
import com.datvexemphim.domain.repository.ShowtimeRepository;
import com.datvexemphim.domain.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class AdminShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final TicketRepository ticketRepository;

    public AdminShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository, RoomRepository roomRepository, TicketRepository ticketRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public List<AdminShowtimeDto> list() {
        // Hiển thị tất cả suất chiếu (ACTIVE và ENDED)
        return showtimeRepository.findAll().stream()
                .map(s -> new AdminShowtimeDto(
                        s.getId(),
                        s.getMovie().getId(),
                        s.getMovie().getTitle(),
                        s.getRoom().getId(),
                        s.getRoom().getName(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getPrice()
                ))
                .toList();
    }

    @Transactional
    public Showtime get(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found"));
    }

    public Showtime create(ShowtimeUpsertRequest req) {
        Showtime st = new Showtime();
        st.setStatus(ShowtimeStatus.ACTIVE); // Mặc định là ACTIVE
        apply(st, req);
        return showtimeRepository.save(st);
    }

    public Showtime update(Long id, ShowtimeUpsertRequest req) {
        Showtime st = get(id);
        apply(st, req);
        return showtimeRepository.save(st);
    }

    public void delete(Long id) {
        showtimeRepository.deleteById(id);
    }

    @Transactional
    public void end(Long id) {
        Showtime st = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found"));
        st.setStatus(ShowtimeStatus.ENDED);
        st.setCancelledAt(Instant.now());
        
        // Mark all tickets as cancelled for this showtime
        List<Ticket> tickets = ticketRepository.findAll().stream()
                .filter(t -> t.getShowtime().getId().equals(id) && !t.getStatus().equals(TicketStatus.CANCELLED))
                .toList();
        
        for (Ticket t : tickets) {
            t.setStatus(TicketStatus.CANCELLED);
            t.setCancelledAt(Instant.now());
            ticketRepository.save(t);
        }
        
        showtimeRepository.save(st);
    }

    private void apply(Showtime st, ShowtimeUpsertRequest req) {
        Movie movie = movieRepository.findById(req.movieId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie not found"));
        Room room = roomRepository.findById(req.roomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found"));
        st.setMovie(movie);
        st.setRoom(room);
        st.setStartTime(req.startTime());
        st.setEndTime(req.endTime());
        st.setPrice(req.price());
    }
}

