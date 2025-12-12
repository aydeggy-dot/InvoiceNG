package com.invoiceng.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "conversation_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(name = "direction", nullable = false, length = 10)
    private String direction; // 'inbound' or 'outbound'

    @Column(name = "message_type", length = 20)
    @Builder.Default
    private String messageType = "text";

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "media_url", columnDefinition = "TEXT")
    private String mediaUrl;

    @Column(name = "whatsapp_message_id", length = 100)
    private String whatsappMessageId;

    @Column(name = "intent_detected", length = 50)
    private String intentDetected;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "entities_extracted", columnDefinition = "jsonb")
    private Map<String, Object> entitiesExtracted;

    @Column(name = "ai_confidence", precision = 3, scale = 2)
    private BigDecimal aiConfidence;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public static ConversationMessage createInbound(Conversation conversation, String content, String messageType, String whatsappMessageId) {
        return ConversationMessage.builder()
                .conversation(conversation)
                .direction("inbound")
                .content(content)
                .messageType(messageType)
                .whatsappMessageId(whatsappMessageId)
                .build();
    }

    public static ConversationMessage createOutbound(Conversation conversation, String content, String messageType, String whatsappMessageId) {
        return ConversationMessage.builder()
                .conversation(conversation)
                .direction("outbound")
                .content(content)
                .messageType(messageType)
                .whatsappMessageId(whatsappMessageId)
                .build();
    }

    public boolean isInbound() {
        return "inbound".equals(direction);
    }

    public boolean isOutbound() {
        return "outbound".equals(direction);
    }
}
