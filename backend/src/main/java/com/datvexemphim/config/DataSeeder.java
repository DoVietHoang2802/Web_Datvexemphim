package com.datvexemphim.config;

import com.datvexemphim.domain.entity.*;
import com.datvexemphim.domain.enums.Role;
import com.datvexemphim.domain.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      MovieRepository movieRepository,
                      RoomRepository roomRepository,
                      SeatRepository seatRepository,
                      ShowtimeRepository showtimeRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        Room room1 = seedRoomAndSeats();
        Room room2 = seedRoom2AndSeats();
        Movie movie = seedMovie();
        seedShowtime(movie, room1);
        seedShowtime2(movie, room2);
    }

    private void seedAdmin() {
        String email = "admin@local";
        if (userRepository.existsByEmail(email)) return;
        User admin = new User();
        admin.setFullName("Administrator");
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    private Room seedRoomAndSeats() {
        Room room = roomRepository.findAll().stream().findFirst().orElse(null);
        if (room == null) {
            room = new Room();
            room.setName("Room 1");
            room.setTotalRows(8);
            room.setTotalCols(10);
            room = roomRepository.save(room);
        }

        if (seatRepository.findByRoomIdOrderByRowLabelAscColNumberAsc(room.getId()).isEmpty()) {
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
        return room;
    }

    private Movie seedMovie() {
        Movie movie = movieRepository.findAll().stream().findFirst().orElse(null);
        if (movie != null) return movie;
        movie = new Movie();
        movie.setTitle("Dune: Part Two");
        movie.setDescription("Sample movie data for local demo.");
        movie.setDurationMinutes(166);
        movie.setPosterUrl("https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?w=900");
        movie.setTrailerUrl("https://www.youtube.com/watch?v=U2Qp5pL3ovA");
        movie.setRating("PG-13");
        movie.setActive(true);
        return movieRepository.save(movie);
    }

    private void seedShowtime(Movie movie, Room room) {
        if (!showtimeRepository.findByStartTimeAfterOrderByStartTimeAsc(Instant.now()).isEmpty()) return;
        Showtime st = new Showtime();
        st.setMovie(movie);
        st.setRoom(room);
        Instant start = Instant.now().plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MINUTES);
        st.setStartTime(start);
        st.setEndTime(start.plus(movie.getDurationMinutes(), ChronoUnit.MINUTES));
        st.setPrice(90000L);
        showtimeRepository.save(st);
    }

    private Room seedRoom2AndSeats() {
        Room room = roomRepository.findById(2L).orElse(null);
        if (room == null) {
            room = new Room();
            room.setName("Room 2");
            room.setTotalRows(8);
            room.setTotalCols(8);
            room = roomRepository.save(room);
        }

        if (seatRepository.findByRoomIdOrderByRowLabelAscColNumberAsc(room.getId()).isEmpty()) {
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
        return room;
    }

    private void seedShowtime2(Movie movie, Room room) {
        long count = showtimeRepository.findByRoomId(room.getId()).size();
        if (count > 0) return;
        Showtime st = new Showtime();
        st.setMovie(movie);
        st.setRoom(room);
        Instant start = Instant.now().plus(4, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MINUTES);
        st.setStartTime(start);
        st.setEndTime(start.plus(movie.getDurationMinutes(), ChronoUnit.MINUTES));
        st.setPrice(85000L);
        showtimeRepository.save(st);
    }
}

