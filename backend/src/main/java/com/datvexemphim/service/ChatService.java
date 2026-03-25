package com.datvexemphim.service;

import com.datvexemphim.api.dto.market.ChatMessageDTO;
import com.datvexemphim.api.dto.market.SendChatRequest;
import com.datvexemphim.domain.entity.ChatMessage;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.enums.TicketStatus;
import com.datvexemphim.domain.repository.ChatMessageRepository;
import com.datvexemphim.domain.repository.TicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final TicketRepository ticketRepository;

    public ChatService(ChatMessageRepository chatMessageRepository, TicketRepository ticketRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.ticketRepository = ticketRepository;
    }

    /**
     * Gửi tin nhắn trong cuộc trò chuyện về 1 vé
     */
    @Transactional
    public ChatMessageDTO sendMessage(SendChatRequest req, User sender) {
        Ticket ticket = ticketRepository.findById(req.ticketId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vé không tồn tại."));

        // Chỉ vé đang rao bán (AVAILABLE) mới chat được
        if (ticket.getStatus() != TicketStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ vé đang rao bán mới chat được.");
        }

        ChatMessage msg = new ChatMessage();
        msg.setTicket(ticket);
        msg.setSender(sender);
        msg.setContent(req.content().trim());

        ChatMessage saved = chatMessageRepository.save(msg);
        return toDTO(saved);
    }

    /**
     * Lấy lịch sử chat của 1 vé
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatHistory(Long ticketId, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vé không tồn tại."));

        return chatMessageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
                .map(this::toDTO)
                .toList();
    }

    private ChatMessageDTO toDTO(ChatMessage m) {
        return new ChatMessageDTO(
                m.getId(),
                m.getTicket().getId(),
                m.getSender().getId(),
                m.getSender().getFullName(),
                m.getContent(),
                m.getCreatedAt()
        );
    }
}
