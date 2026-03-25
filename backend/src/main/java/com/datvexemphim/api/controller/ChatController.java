package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.market.ChatMessageDTO;
import com.datvexemphim.api.dto.market.SendChatRequest;
import com.datvexemphim.service.ChatService;
import com.datvexemphim.service.CurrentUserService;
import com.datvexemphim.domain.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final CurrentUserService currentUserService;

    public ChatController(ChatService chatService, CurrentUserService currentUserService) {
        this.chatService = chatService;
        this.currentUserService = currentUserService;
    }

    /** Gửi tin nhắn */
    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(@Valid @RequestBody SendChatRequest req) {
        User me = currentUserService.requireUser();
        ChatMessageDTO msg = chatService.sendMessage(req, me);
        return ResponseEntity.ok(msg);
    }

    /** Lấy lịch sử chat của 1 vé */
    @GetMapping("/history/{ticketId}")
    public List<ChatMessageDTO> getHistory(@PathVariable Long ticketId) {
        User me = currentUserService.requireUser();
        return chatService.getChatHistory(ticketId, me);
    }
}
