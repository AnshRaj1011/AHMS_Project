package com.ahms.controller;

import com.ahms.entity.ChatMessage;
import com.ahms.service.ChatService;
import lombok.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/chat") @CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService svc;
    private final SimpMessagingTemplate broker;

    // ─── REST endpoints ───────────────────────────────────────────────────────

    /** Start a new chat session */
    @PostMapping("/session")
    public ResponseEntity<?> newSession() {
        return ResponseEntity.ok(Map.of("sessionId", svc.newSession()));
    }

    /** Get full message history for a session */
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<?> history(@PathVariable String sessionId) {
        return ResponseEntity.ok(Map.of("success", true, "data", svc.getHistory(sessionId)));
    }

    /** Get unread messages for a user */
    @GetMapping("/unread/{userId}")
    public ResponseEntity<?> unread(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("success", true, "data", svc.getUnread(userId)));
    }

    /** Mark all messages as read for a user */
    @PatchMapping("/read/{userId}")
    public ResponseEntity<?> markRead(@PathVariable Long userId) {
        svc.markRead(userId); return ResponseEntity.ok(Map.of("success", true));
    }

    /** Chatbot endpoint (REST fallback — works without WebSocket) */
    @PostMapping("/bot")
    public ResponseEntity<?> bot(@RequestBody Map<String, String> body) {
        String reply = svc.getBotReply(body.get("message"));
        return ResponseEntity.ok(Map.of("success", true, "reply", reply));
    }

    // ─── WebSocket message handlers ───────────────────────────────────────────

    /** Live consultation: patient ↔ doctor over WebSocket */
    @MessageMapping("/chat.send")
    public void handleMessage(WsMessage msg) {
        ChatMessage saved = svc.save(msg.sessionId, msg.senderId, msg.senderName,
                msg.receiverId, ChatMessage.SenderType.valueOf(msg.senderType), msg.message);

        // Deliver to the session topic (both users subscribed to this)
        broker.convertAndSend("/topic/chat/" + msg.sessionId, saved);

        // Also deliver to the specific receiver's queue
        if (msg.receiverId != null)
            broker.convertAndSendToUser(msg.receiverId.toString(), "/queue/messages", saved);
    }

    /** Bot over WebSocket */
    @MessageMapping("/chat.bot")
    public void handleBotMessage(WsMessage msg) {
        // Echo user message first
        ChatMessage userMsg = svc.save(msg.sessionId, msg.senderId, msg.senderName,
                null, ChatMessage.SenderType.PATIENT, msg.message);
        broker.convertAndSend("/topic/chat/" + msg.sessionId, userMsg);

        // Get and send bot reply
        String reply = svc.getBotReply(msg.message);
        ChatMessage botMsg = svc.save(msg.sessionId, null, "AHMS Bot",
                msg.senderId, ChatMessage.SenderType.BOT, reply);
        broker.convertAndSend("/topic/chat/" + msg.sessionId, botMsg);
    }

    @Data
    static class WsMessage {
        String sessionId;
        Long   senderId;
        String senderName;
        Long   receiverId;
        String senderType;  // PATIENT, DOCTOR, BOT
        String message;
    }
}
