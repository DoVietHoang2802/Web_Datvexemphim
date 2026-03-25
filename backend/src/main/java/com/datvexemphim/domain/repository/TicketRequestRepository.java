package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.TicketRequest;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.enums.TicketRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRequestRepository extends JpaRepository<TicketRequest, Long> {

    /** Tìm request của 1 vé theo người yêu cầu */
    Optional<TicketRequest> findByTicketIdAndRequesterId(Long ticketId, Long requesterId);

    /** Danh sách request của 1 vé */
    List<TicketRequest> findByTicketIdAndStatus(Long ticketId, TicketRequestStatus status);

    /** Danh sách request đang chờ của 1 vé */
    List<TicketRequest> findByTicketIdAndStatusOrderByCreatedAtAsc(Long ticketId, TicketRequestStatus status);

    /** Request của 1 user cho 1 vé */
    Optional<TicketRequest> findByTicketAndRequester(Ticket ticket, User requester);

    /** Kiểm tra user đã request vé này chưa */
    boolean existsByTicketIdAndRequesterIdAndStatus(Long ticketId, Long requesterId, TicketRequestStatus status);

    /** Xóa tất cả request của 1 vé */
    void deleteByTicketId(Long ticketId);

    /** Danh sách request của 1 user theo trạng thái */
    List<TicketRequest> findByRequesterIdAndStatusOrderByCreatedAtDesc(Long requesterId, TicketRequestStatus status);

    /** Tất cả request của 1 user */
    List<TicketRequest> findByRequesterIdOrderByCreatedAtDesc(Long requesterId);
}
