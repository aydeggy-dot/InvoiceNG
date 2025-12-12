package com.invoiceng.dto.response;

import com.invoiceng.entity.ConversationMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessageResponse {

    private UUID id;
    private UUID conversationId;
    private String direction;
    private String messageType;
    private String content;
    private String mediaUrl;
    private String whatsappMessageId;
    private String intentDetected;
    private Map<String, Object> entitiesExtracted;
    private BigDecimal aiConfidence;
    private LocalDateTime createdAt;

    public static ConversationMessageResponse fromEntity(ConversationMessage message) {
        return ConversationMessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .direction(message.getDirection())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .mediaUrl(message.getMediaUrl())
                .whatsappMessageId(message.getWhatsappMessageId())
                .intentDetected(message.getIntentDetected())
                .entitiesExtracted(message.getEntitiesExtracted())
                .aiConfidence(message.getAiConfidence())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
