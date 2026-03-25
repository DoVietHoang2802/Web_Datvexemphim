package com.datvexemphim.service.admin;

import com.datvexemphim.api.dto.admin.AdminShowtimeDto;
import com.datvexemphim.api.dto.admin.ShowtimeUpsertRequest;
import com.datvexemphim.domain.entity.Movie;
import com.datvexemphim.domain.entity.Room;
import com.datvexemphim.domain.entity.Seat;
import com.datvexemphim.domain.entity.Showtime;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.enums.ShowtimeStatus;
import com.datvexemphim.domain.enums.TicketStatus;
import com.datvexemphim.domain.repository.ChatMessageRepository;
import com.datvexemphim.domain.repository.FoodOrderRepository;
import com.datvexemphim.domain.repository.MovieRepository;
import com.datvexemphim.domain.repository.RoomRepository;
import com.datvexemphim.domain.repository.SeatRepository;
import com.datvexemphim.domain.repository.ShowtimeRepository;
import com.datvexemphim.domain.repository.TicketRepository;
import com.datvexemphim.domain.repository.TicketRequestRepository;
import com.datvexemphim.domain.repository.TransferHistoryRepository;
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
    private final TicketRequestRepository ticketRequestRepository;
    private final TransferHistoryRepository transferHistoryRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final SeatRepository seatRepository;

    public AdminShowtimeService(ShowtimeRepository showtimeRepository,
                                MovieRepository movieRepository,
                                RoomRepository roomRepository,
                                TicketRepository ticketRepository,
                                TicketRequestRepository ticketRequestRepository,
                                TransferHistoryRepository transferHistoryRepository,
                                ChatMessageRepository chatMessageRepository,
                                FoodOrderRepository foodOrderRepository,
                                SeatRepository seatRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.ticketRepository = ticketRepository;
        this.ticketRequestRepository = ticketRequestRepository;
        this.transferHistoryRepository = transferHistoryRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.foodOrderRepository = foodOrderRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public List<AdminShowtimeDto> list() {
        return showtimeRepository.findAll().stream()
                .map(s -> new AdminShowtimeDto(
                        s.getId(),
                        s.getMovie().getId(),
                        s.getMovie().getTitle(),
                        s.getRoom().getId(),
                        s.getRoom().getName(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getPrice(),
                        s.getStatus().name()
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
        st.setStatus(ShowtimeStatus.ACTIVE);
        apply(st, req);
        return showtimeRepository.save(st);
    }

    public Showtime update(Long id, ShowtimeUpsertRequest req) {
        Showtime st = get(id);
        if (st.getStatus() == ShowtimeStatus.ENDED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Suất chiếu đã ngừng, không thể chỉnh sửa.");
        }
        apply(st, req);
        return showtimeRepository.save(st);
    }

    @Transactional
    public void delete(Long id) {
        Showtime st = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found"));

        List<Ticket> tickets = ticketRepository.findByShowtimeId(id);
        for (Ticket t : tickets) {
            Long ticketId = t.getId();

            // Giải phóng ghế
            Seat seat = t.getSeat();
            if (seat != null) {
                seat.setActive(true);
                seatRepository.save(seat);
            }

            // Xóa phụ thuộc trước để tránh lỗi FK 409
            try { ticketRequestRepository.deleteByTicketId(ticketId); } catch (Exception ignored) {}
            try { chatMessageRepository.deleteByTicketId(ticketId); } catch (Exception ignored) {}
            try { transferHistoryRepository.deleteByTicketId(ticketId); } catch (Exception ignored) {}
            try { foodOrderRepository.deleteByTicketId(ticketId); } catch (Exception ignored) {}

            // bỏ liên kết payment trước khi xóa
            if (t.getPayment() != null) {
                t.setPayment(null);
                ticketRepository.save(t);
            }
        }

        ticketRepository.flush();
        ticketRepository.deleteAll(tickets);
        ticketRepository.flush();

        st.setStatus(ShowtimeStatus.ENDED);
        st.setCancelledAt(Instant.now());
        showtimeRepository.save(st);
        showtimeRepository.delete(st);
    }

    @Transactional
    public void end(Long id) {
        Showtime st = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found"));

        st.setStatus(ShowtimeStatus.ENDED);
        st.setCancelledAt(Instant.now());

        List<Ticket> tickets = ticketRepository.findByShowtimeId(id);
        for (Ticket t : tickets) {
            if (t.getStatus() != TicketStatus.CANCELLED) {
                t.setStatus(TicketStatus.CANCELLED);
                t.setCancelledAt(Instant.now());

                Seat seat = t.getSeat();
                if (seat != null) {
                    seat.setActive(true);
                    seatRepository.save(seat);
                }

                ticketRepository.save(t);
            }
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
