package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.market.CommunityMessageDTO;
import com.datvexemphim.api.dto.market.SendCommunityMessageRequest;
import com.datvexemphim.domain.entity.CommunityMessage;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.repository.CommunityMessageRepository;
import com.datvexemphim.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityMessageRepository messageRepository;
    private final CurrentUserService currentUserService;

    public CommunityController(CommunityMessageRepository messageRepository, CurrentUserService currentUserService) {
        this.messageRepository = messageRepository;
        this.currentUserService = currentUserService;
    }

    /** Lấy tin nhắn cộng đồng */
    @GetMapping("/messages")
    public List<CommunityMessageDTO> getMessages() {
        return messageRepository.findLatest().stream()
                .map(this::toDTO)
                .toList();
    }

    /** Gửi tin nhắn cộng đồng */
    @PostMapping("/messages")
    public ResponseEntity<CommunityMessageDTO> sendMessage(@Valid @RequestBody SendCommunityMessageRequest req) {
        User me = currentUserService.requireUser();

        CommunityMessage msg = new CommunityMessage();
        msg.setSender(me);
        msg.setContent(req.content().trim());

        CommunityMessage saved = messageRepository.save(msg);
        return ResponseEntity.ok(toDTO(saved));
    }

    private CommunityMessageDTO toDTO(CommunityMessage m) {
        return new CommunityMessageDTO(
                m.getId(),
                m.getSender().getId(),
                m.getSender().getFullName(),
                m.getContent(),
                m.getCreatedAt()
        );
    }
}
