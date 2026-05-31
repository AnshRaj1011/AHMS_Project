package com.ahms.repository;
import com.ahms.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySessionIdOrderBySentAtAsc(String sessionId);
    List<ChatMessage> findByReceiverIdAndIsReadFalse(Long receiverId);
}
