package com.datvexemphim.service;

import com.datvexemphim.api.dto.market.*;
import com.datvexemphim.domain.entity.*;
import com.datvexemphim.domain.enums.*;
import com.datvexemphim.domain.repository.*;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class TicketMarketService {

    private final TicketRepository ticketRepository;
    private final TicketRequestRepository ticketRequestRepository;
    private final FoodOrderService foodOrderService;

    public TicketMarketService(TicketRepository ticketRepository,
                               TicketRequestRepository ticketRequestRepository,
                               FoodOrderService foodOrderService) {
        this.ticketRepository = ticketRepository;
        this.ticketRequestRepository = ticketRequestRepository;
        this.foodOrderService = foodOrderService;
    }

    // ===== 1. LẤY DANH SÁCH VÉ ĐANG BÁN =====

    /**
     * Lấy tất cả vé đang rao bán (AVAILABLE)
     */
    @Transactional(readOnly = true)
    public List<TicketMarketDTO> getAvailableTickets() {
        return ticketRepository.findByStatus(TicketStatus.AVAILABLE).stream()
                .map(this::toMarketDTO)
                .toList();
    }

    /**
     * Lấy vé đang bán của 1 user (chính mình đăng)
     */
    @Transactional(readOnly = true)
    public List<TicketMarketDTO> getMyListedTickets(Long userId) {
        return ticketRepository.findByOwnerIdAndStatus(userId, TicketStatus.AVAILABLE).stream()
                .map(this::toMarketDTO)
                .toList();
    }

    // ===== 2. ĐĂNG VÉ LÊN CHỢ =====

    /**
     * Đăng vé lên chợ (CONFIRMED -> AVAILABLE)
     */
    @Transactional
    public void listTicket(Long ticketId, User seller) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vé không tồn tại."));

        // Kiểm tra quyền sở hữu
        if (!ticket.getOwner().getId().equals(seller.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không sở hữu vé này.");
        }

        // Chỉ vé CONFIRMED mới được đăng bán
        if (ticket.getStatus() != TicketStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chỉ vé đã thanh toán (CONFIRMED) mới được đăng bán.");
        }

        // Suất chiếu phải chưa bắt đầu
        if (!ticket.getShowtime().getStartTime().isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Suất chiếu đã bắt đầu, không thể đăng bán.");
        }

        ticket.setStatus(TicketStatus.AVAILABLE);
        ticketRepository.save(ticket);
    }

    /**
     * Gỡ vé khỏi chợ (AVAILABLE -> CONFIRMED)
     */
    @Transactional
    public void unlistTicket(Long ticketId, User seller) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vé không tồn tại."));

        if (!ticket.getOwner().getId().equals(seller.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không sở hữu vé này.");
        }

        if (ticket.getStatus() != TicketStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vé không đang được rao bán.");
        }

        ticket.setStatus(TicketStatus.CONFIRMED);
        ticketRepository.save(ticket);
    }

    // ===== 3. YÊU CẦU NHẬN VÉ =====

    /**
     * Gửi yêu cầu nhận vé (tạo TicketRequest PENDING)
     */
    @Transactional
    public void requestTicket(Long ticketId, User requester) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vé không tồn tại."));

        // Không tự request vé của mình
        if (ticket.getOwner().getId().equals(requester.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể yêu cầu nhận vé của chính mình.");
        }

        // Vé phải đang AVAILABLE
        if (ticket.getStatus() != TicketStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vé không còn đang rao bán.");
        }

        // Kiểm tra đã request chưa
        if (ticketRequestRepository.existsByTicketIdAndRequesterIdAndStatus(
                ticketId, requester.getId(), TicketRequestStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn đã gửi yêu cầu rồi.");
        }

        TicketRequest req = new TicketRequest();
        req.setTicket(ticket);
        req.setRequester(requester);
        req.setStatus(TicketRequestStatus.PENDING);
        ticketRequestRepository.save(req);
    }

    /**
     * Lấy danh sách yêu cầu nhận vé của 1 vé (chủ vé gọi)
     */
    @Transactional(readOnly = true)
    public List<TicketRequestDTO> getRequestsForMyTicket(Long ticketId, User seller) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vé không tồn tại."));

        if (!ticket.getOwner().getId().equals(seller.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không sở hữu vé này.");
        }

        return ticketRequestRepository.findByTicketIdAndStatusOrderByCreatedAtAsc(ticketId, TicketRequestStatus.PENDING)
                .stream()
                .map(this::toRequestDTO)
                .toList();
    }

    /**
     * Lấy yêu cầu nhận vé của tôi
     */
    @Transactional(readOnly = true)
    public List<TicketRequestDTO> getMyRequests(Long userId) {
        return ticketRequestRepository.findByRequesterIdAndStatusOrderByCreatedAtDesc(userId, TicketRequestStatus.PENDING)
                .stream()
                .map(this::toRequestDTO)
                .toList();
    }

    // ===== 4. DUYỆT YÊU CẦU (ĐỒNG Ý / TỪ CHỐI) =====

    /**
     * Duyệt yêu cầu nhận vé - CÓ DÙNG TRANSACTION
     * Đảm bảo chỉ 1 người nhận thành công khi có nhiều request cùng lúc
     */
    @Transactional
    public void respondToRequest(Long requestId, boolean accept, User seller) {
        TicketRequest ticketRequest = ticketRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Yêu cầu không tồn tại."));

        Ticket ticket = ticketRequest.getTicket();

        // Kiểm tra quyền: chỉ chủ vé mới duyệt được
        if (!ticket.getOwner().getId().equals(seller.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không sở hữu vé này.");
        }

        // Phải đang AVAILABLE mới duyệt được
        if (ticket.getStatus() != TicketStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vé không còn đang rao bán.");
        }

        if (accept) {
            // ===== CHUYỂN VÉ =====
            User buyer = ticketRequest.getRequester();

            // Chuyển chủ sở hữu
            ticket.setOwner(buyer);
            ticket.setStatus(TicketStatus.SOLD);
            ticketRepository.save(ticket);

            // Chuyển food order theo vé
            foodOrderService.handleTicketTransfer(ticket.getId(), buyer.getId(), null);

            // Cập nhật request
            ticketRequest.setStatus(TicketRequestStatus.ACCEPTED);
            ticketRequest.setRespondedAt(Instant.now());
            ticketRequestRepository.save(ticketRequest);

            // Từ chối tất cả request khác cho vé này
            ticketRequestRepository.findByTicketIdAndStatusOrderByCreatedAtAsc(ticket.getId(), TicketRequestStatus.PENDING)
                    .forEach(other -> {
                        if (!other.getId().equals(requestId)) {
                            other.setStatus(TicketRequestStatus.REJECTED);
                            other.setRespondedAt(Instant.now());
                            ticketRequestRepository.save(other);
                        }
                    });

        } else {
            // ===== TỪ CHỐI =====
            ticketRequest.setStatus(TicketRequestStatus.REJECTED);
            ticketRequest.setRespondedAt(Instant.now());
            ticketRequestRepository.save(ticketRequest);
        }
    }

    // ===== 5. HELPER =====

    private TicketMarketDTO toMarketDTO(Ticket t) {
        return new TicketMarketDTO(
                t.getId(),
                t.getShowtime().getMovie().getTitle(),
                t.getShowtime().getStartTime(),
                t.getShowtime().getRoom().getName(),
                t.getSeat().getSeatCode(),
                t.getShowtime().getPrice(),
                t.getOwner().getFullName(),
                t.getOwner().getId(),
                t.getStatus().name(),
                t.getBookedAt()
        );
    }

    private TicketRequestDTO toRequestDTO(TicketRequest r) {
        return new TicketRequestDTO(
                r.getId(),
                r.getTicket().getId(),
                r.getRequester().getId(),
                r.getRequester().getFullName(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }
}
