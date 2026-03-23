package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.booking.BookingResponse;
import com.datvexemphim.api.dto.booking.CreateBookingRequest;
import com.datvexemphim.service.BookingService;
import com.datvexemphim.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class BookingController {
    private final BookingService bookingService;
    private final CurrentUserService currentUserService;

    public BookingController(BookingService bookingService, CurrentUserService currentUserService) {
        this.bookingService = bookingService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public BookingResponse book(@Valid @RequestBody CreateBookingRequest req) {
        return bookingService.createPendingTickets(req, currentUserService.requireUser());
    }
}

