package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.market.*;
import com.datvexemphim.service.CurrentUserService;
import com.datvexemphim.service.TicketMarketService;
import com.datvexemphim.domain.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
public class TicketMarketController {

    private final TicketMarketService marketService;
    private final CurrentUserService currentUserService;

    public TicketMarketController(TicketMarketService marketService, CurrentUserService currentUserService) {
        this.marketService = marketService;
        this.currentUserService = currentUserService;
    }

    // ===== 1. LẤY DANH SÁCH VÉ ĐANG BÁN =====

    /** Tất cả vé đang rao bán */
    @GetMapping("/tickets")
    public List<TicketMarketDTO> getAvailableTickets() {
        return marketService.getAvailableTickets();
    }

    /** Vé đang bán của tôi */
    @GetMapping("/my-listed")
    public List<TicketMarketDTO> getMyListedTickets() {
        return marketService.getMyListedTickets(currentUserService.requireUser().getId());
    }

    /** Yêu cầu nhận vé của tôi */
    @GetMapping("/my-requests")
    public List<TicketRequestDTO> getMyRequests() {
        return marketService.getMyRequests(currentUserService.requireUser().getId());
    }

    // ===== 2. ĐĂNG / GỠ VÉ =====

    /** Đăng vé lên chợ */
    @PostMapping("/list")
    public ResponseEntity<Void> listTicket(@Valid @RequestBody CreateTicketListingRequest req) {
        User me = currentUserService.requireUser();
        marketService.listTicket(req.ticketId(), me);
        return ResponseEntity.ok().build();
    }

    /** Gỡ vé khỏi chợ */
    @PostMapping("/unlist")
    public ResponseEntity<Void> unlistTicket(@RequestBody CreateTicketListingRequest req) {
        User me = currentUserService.requireUser();
        marketService.unlistTicket(req.ticketId(), me);
        return ResponseEntity.ok().build();
    }

    // ===== 3. YÊU CẦU NHẬN VÉ =====

    /** Gửi yêu cầu nhận vé */
    @PostMapping("/request")
    public ResponseEntity<Void> requestTicket(@Valid @RequestBody RequestTicketRequest req) {
        User me = currentUserService.requireUser();
        marketService.requestTicket(req.ticketId(), me);
        return ResponseEntity.ok().build();
    }

    // ===== 4. DUYỆT YÊU CẦU =====

    /** Danh sách request chờ duyệt cho 1 vé (chủ vé gọi) */
    @GetMapping("/requests/{ticketId}")
    public List<TicketRequestDTO> getRequestsForTicket(@PathVariable Long ticketId) {
        User me = currentUserService.requireUser();
        return marketService.getRequestsForMyTicket(ticketId, me);
    }

    /** Đồng ý / Từ chối yêu cầu nhận vé */
    @PostMapping("/respond")
    public ResponseEntity<Void> respondToRequest(@Valid @RequestBody RespondRequestRequest req) {
        User me = currentUserService.requireUser();
        marketService.respondToRequest(req.requestId(), req.accept(), me);
        return ResponseEntity.ok().build();
    }

    /** Số thông báo (yêu cầu chờ duyệt) */
    @GetMapping("/notifications")
    public ResponseEntity<TicketMarketService.NotificationCounts> getNotifications() {
        Long userId = currentUserService.requireUser().getId();
        return ResponseEntity.ok(marketService.getNotificationCounts(userId));
    }
}
