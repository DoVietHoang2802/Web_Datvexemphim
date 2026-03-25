package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.market.CommunityMessageDTO;
import com.datvexemphim.api.dto.market.SendCommunityMessageRequest;
import com.datvexemphim.domain.entity.CommunityMessage;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.repository.CommunityMessageRepository;
import com.datvexemphim.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityMessageRepository messageRepository;
    private final CurrentUserService currentUserService;

    // SSE clients online
    private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();

    public CommunityController(CommunityMessageRepository messageRepository, CurrentUserService currentUserService) {
        this.messageRepository = messageRepository;
        this.currentUserService = currentUserService;
    }

    /** Lấy lịch sử tin nhắn cộng đồng (mới nhất 100 tin, theo thứ tự cũ -> mới) */
    @Transactional(readOnly = true)
    @GetMapping("/messages")
    public List<CommunityMessageDTO> getMessages() {
        List<CommunityMessage> latest = messageRepository.findTop100ByOrderByCreatedAtDesc();
        List<CommunityMessageDTO> dto = latest.stream().map(this::toDTO).toList();
        Collections.reverse(dto);
        return dto;
    }

    /** SSE stream realtime chat công khai */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        // timeout 0 = no timeout (server-managed)
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((ex) -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("ok"));

            emitter.send(SseEmitter.event()
                    .name("online")
                    .data(emitters.size()));
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    /** Gửi tin nhắn cộng đồng */
    @PostMapping("/messages")
    public ResponseEntity<CommunityMessageDTO> sendMessage(@Valid @RequestBody SendCommunityMessageRequest req) {
        User me = currentUserService.requireUser();

        CommunityMessage msg = new CommunityMessage();
        msg.setSender(me);
        msg.setContent(req.content().trim());
        msg.setCreatedAt(Instant.now());

        CommunityMessage saved = messageRepository.save(msg);
        CommunityMessageDTO dto = toDTO(saved);

        broadcastNewMessage(dto);
        return ResponseEntity.ok(dto);
    }

    /** Số user online trong chat (xấp xỉ theo số kết nối SSE) */
    @GetMapping("/online")
    public ResponseEntity<Integer> getOnline() {
        return ResponseEntity.ok(emitters.size());
    }

    private void broadcastNewMessage(CommunityMessageDTO dto) {
        List<SseEmitter> dead = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("message").data(dto));
                emitter.send(SseEmitter.event().name("online").data(emitters.size()));
            } catch (Exception e) {
                dead.add(emitter);
            }
        }

        emitters.removeAll(dead);
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
