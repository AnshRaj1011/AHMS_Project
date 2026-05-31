package com.ahms.service;

import com.ahms.entity.ChatMessage;
import com.ahms.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository repo;
    private final WebClient.Builder webClientBuilder;

    @Value("${python.chatbot-service.url}")
    private String chatbotUrl;

    @Transactional
    public ChatMessage save(String sessionId, Long senderId, String senderName,
                             Long receiverId, ChatMessage.SenderType type, String message) {
        return repo.save(ChatMessage.builder()
                .sessionId(sessionId).senderId(senderId).senderName(senderName)
                .receiverId(receiverId).senderType(type).message(message).build());
    }

    public List<ChatMessage> getHistory(String sessionId) {
        return repo.findBySessionIdOrderBySentAtAsc(sessionId);
    }

    public List<ChatMessage> getUnread(Long userId) {
        return repo.findByReceiverIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markRead(Long userId) {
        List<ChatMessage> unread = repo.findByReceiverIdAndIsReadFalse(userId);
        unread.forEach(m -> m.setIsRead(true));
        repo.saveAll(unread);
    }

    public String newSession() { return UUID.randomUUID().toString(); }

    /**
     * Calls the Python chatbot microservice.
     * Falls back to a built-in rule-based response if Python is unreachable.
     */
    public String getBotReply(String userMessage) {
        try {
            WebClient client = webClientBuilder.baseUrl(chatbotUrl).build();
            Map response = client.post().uri("/chat")
                    .bodyValue(Map.of("message", userMessage))
                    .retrieve().bodyToMono(Map.class).block();
            if (response != null && response.containsKey("reply"))
                return (String) response.get("reply");
        } catch (Exception ignored) {}
        return fallbackBotReply(userMessage);
    }

    /** Built-in fallback — keyword-based responses */
    private String fallbackBotReply(String msg) {
        String m = msg.toLowerCase();
        if (m.contains("appointment"))  return "You can book an appointment from the Appointments section. Choose your doctor, date, and time slot.";
        if (m.contains("bed") || m.contains("admit")) return "Bed availability can be checked in the Beds section. For ICU emergencies, please use the ICU Request form.";
        if (m.contains("icu"))          return "ICU requests are handled by our admin team 24/7. Please submit an ICU Emergency Request with your medical condition.";
        if (m.contains("ambulance"))    return "For ambulance, go to Emergency > Request Ambulance and share your location. Our team will dispatch immediately.";
        if (m.contains("lab") || m.contains("test")) return "You can book lab tests (Blood Test, MRI, CT Scan, X-Ray) from the Lab Tests section.";
        if (m.contains("doctor"))       return "Browse our doctors by specialization in the Doctors section and book an appointment directly.";
        if (m.contains("emergency"))    return "For medical emergencies, call 112. You can also request an ambulance through our Emergency module.";
        if (m.contains("hello") || m.contains("hi")) return "Hello! I'm AHMS Assistant. I can help you with appointments, bed availability, lab tests, and emergencies. How can I help?";
        return "I'm here to help! You can ask me about appointments, bed availability, lab tests, ICU requests, ambulance services, or finding doctors.";
    }
}
