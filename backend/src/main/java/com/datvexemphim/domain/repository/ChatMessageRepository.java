package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /** Tin nhắn của 1 vé, sắp xếp theo thời gian */
    @Query("SELECT c FROM ChatMessage c WHERE c.ticket.id = :ticketId ORDER BY c.createdAt ASC")
    List<ChatMessage> findByTicketIdOrderByCreatedAtAsc(@Param("ticketId") Long ticketId);

    /** Tin nhắn mới nhất của 1 vé */
    @Query("SELECT c FROM ChatMessage c WHERE c.ticket.id = :ticketId ORDER BY c.createdAt DESC LIMIT 1")
    ChatMessage findLatestByTicketId(@Param("ticketId") Long ticketId);
}
