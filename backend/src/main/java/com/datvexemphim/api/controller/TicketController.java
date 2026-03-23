package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.ticket.TicketHistoryDto;
import com.datvexemphim.api.dto.ticket.TransferTicketRequest;
import com.datvexemphim.service.CurrentUserService;
import com.datvexemphim.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin
public class TicketController {
    private final TicketService ticketService;
    private final CurrentUserService currentUserService;

    public TicketController(TicketService ticketService, CurrentUserService currentUserService) {
        this.ticketService = ticketService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public List<TicketHistoryDto> myTickets() {
        return ticketService.myTickets(currentUserService.requireUser());
    }

    @GetMapping
    public List<TicketHistoryDto> getByIds(@RequestParam List<Long> ids) {
        return ticketService.getByIds(ids, currentUserService.requireUser());
    }

    @PostMapping("/{ticketId}/cancel")
    public void cancel(@PathVariable Long ticketId) {
        ticketService.cancel(ticketId, currentUserService.requireUser());
    }

    @PostMapping("/transfer")
    public void transfer(@Valid @RequestBody TransferTicketRequest req) {
        ticketService.transfer(req, currentUserService.requireUser());
    }

    @DeleteMapping("/{ticketId}")
    public void delete(@PathVariable Long ticketId) {
        ticketService.delete(ticketId, currentUserService.requireUser());
    }
}

