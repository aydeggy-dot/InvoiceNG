package com.invoiceng.dto.response;

import com.invoiceng.entity.Conversation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {

    private UUID id;
    private String customerPhone;
    private String customerName;
    private String customerWhatsappId;
    private String state;
    private Map<String, Object> context;
    private Object cart;
    private Boolean isActive;
    private LocalDateTime lastMessageAt;
    private Integer messageCount;
    private Boolean isHandedOff;
    private LocalDateTime handedOffAt;
    private String handedOffReason;
    private String outcome;
    private UUID orderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ConversationResponse fromEntity(Conversation conversation) {
        return ConversationResponse.builder()
                .id(conversation.getId())
                .customerPhone(conversation.getCustomerPhone())
                .customerName(conversation.getCustomerName())
                .customerWhatsappId(conversation.getCustomerWhatsappId())
                .state(conversation.getState())
                .context(conversation.getContext())
                .cart(conversation.getCart())
                .isActive(conversation.getIsActive())
                .lastMessageAt(conversation.getLastMessageAt())
                .messageCount(conversation.getMessageCount())
                .isHandedOff(conversation.getIsHandedOff())
                .handedOffAt(conversation.getHandedOffAt())
                .handedOffReason(conversation.getHandedOffReason())
                .outcome(conversation.getOutcome())
                .orderId(conversation.getOrderId())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    public static ConversationResponse fromEntityBasic(Conversation conversation) {
        return ConversationResponse.builder()
                .id(conversation.getId())
                .customerPhone(conversation.getCustomerPhone())
                .customerName(conversation.getCustomerName())
                .state(conversation.getState())
                .isActive(conversation.getIsActive())
                .lastMessageAt(conversation.getLastMessageAt())
                .messageCount(conversation.getMessageCount())
                .isHandedOff(conversation.getIsHandedOff())
                .outcome(conversation.getOutcome())
                .createdAt(conversation.getCreatedAt())
                .build();
    }
}
