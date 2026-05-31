package com.ahms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "chat_messages")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(name = "session_id", nullable = false) private String sessionId;
    @Column(name = "sender_id") private Long senderId;
    @Column(name = "sender_name") private String senderName;
    @Column(name = "receiver_id") private Long receiverId;

    @Enumerated(EnumType.STRING) private SenderType senderType;

    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    private LocalDateTime sentAt;
    @Builder.Default private Boolean isRead = false;

    @PrePersist protected void onCreate() { sentAt = LocalDateTime.now(); }

    public enum SenderType { PATIENT, DOCTOR, BOT }
}
