package com.invoiceng.service;

import com.invoiceng.dto.whatsapp.WhatsAppWebhookPayload;
import com.invoiceng.entity.Conversation;
import com.invoiceng.entity.ConversationMessage;
import com.invoiceng.entity.User;
import com.invoiceng.repository.ConversationMessageRepository;
import com.invoiceng.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository messageRepository;

    /**
     * Get or create a conversation for a customer with a business
     */
    @Transactional
    public Conversation getOrCreateConversation(User business, String customerPhone, String customerName, String customerWhatsappId) {
        Optional<Conversation> existing = conversationRepository.findByBusinessIdAndCustomerPhone(business.getId(), customerPhone);

        if (existing.isPresent()) {
            Conversation conversation = existing.get();
            // Update customer info if we have new details
            if (customerName != null && !customerName.isEmpty()) {
                conversation.setCustomerName(customerName);
            }
            if (customerWhatsappId != null && !customerWhatsappId.isEmpty()) {
                conversation.setCustomerWhatsappId(customerWhatsappId);
            }
            // Reactivate if was inactive
            if (!conversation.getIsActive()) {
                conversation.setIsActive(true);
                conversation.setState("greeting");
            }
            return conversationRepository.save(conversation);
        }

        // Create new conversation
        Conversation conversation = Conversation.builder()
                .business(business)
                .customerPhone(customerPhone)
                .customerName(customerName)
                .customerWhatsappId(customerWhatsappId)
                .state("greeting")
                .isActive(true)
                .messageCount(0)
                .context(new HashMap<>())
                .build();

        conversation = conversationRepository.save(conversation);
        log.info("Created new conversation {} for business {} with customer {}", conversation.getId(), business.getId(), customerPhone);

        return conversation;
    }

    /**
     * Save an inbound message from WhatsApp
     */
    @Transactional
    public ConversationMessage saveInboundMessage(Conversation conversation, WhatsAppWebhookPayload.Message message) {
        // Check for duplicate
        if (message.getId() != null && messageRepository.existsByWhatsappMessageId(message.getId())) {
            log.debug("Skipping duplicate message: {}", message.getId());
            return null;
        }

        String content = extractMessageContent(message);
        String messageType = message.getType() != null ? message.getType() : "text";

        ConversationMessage conversationMessage = ConversationMessage.createInbound(
                conversation,
                content,
                messageType,
                message.getId()
        );

        // Set media URL if applicable
        if ("image".equals(messageType) && message.getImage() != null) {
            conversationMessage.setMediaUrl(message.getImage().getId());
        } else if ("audio".equals(messageType) && message.getAudio() != null) {
            conversationMessage.setMediaUrl(message.getAudio().getId());
        } else if ("document".equals(messageType) && message.getDocument() != null) {
            conversationMessage.setMediaUrl(message.getDocument().getId());
        }

        conversationMessage = messageRepository.save(conversationMessage);

        // Update conversation
        conversation.incrementMessageCount();
        conversationRepository.save(conversation);

        log.debug("Saved inbound message {} for conversation {}", conversationMessage.getId(), conversation.getId());

        return conversationMessage;
    }

    /**
     * Save an outbound message sent to WhatsApp
     */
    @Transactional
    public ConversationMessage saveOutboundMessage(Conversation conversation, String content, String messageType, String whatsappMessageId) {
        ConversationMessage message = ConversationMessage.createOutbound(
                conversation,
                content,
                messageType,
                whatsappMessageId
        );

        message = messageRepository.save(message);

        // Update conversation
        conversation.incrementMessageCount();
        conversationRepository.save(conversation);

        log.debug("Saved outbound message {} for conversation {}", message.getId(), conversation.getId());

        return message;
    }

    /**
     * Check if a message has already been processed
     */
    public boolean isMessageProcessed(String whatsappMessageId) {
        return whatsappMessageId != null && messageRepository.existsByWhatsappMessageId(whatsappMessageId);
    }

    /**
     * Update conversation state
     */
    @Transactional
    public Conversation updateState(UUID conversationId, String newState) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        conversation.setState(newState);
        return conversationRepository.save(conversation);
    }

    /**
     * Update conversation state and context
     */
    @Transactional
    public Conversation updateStateAndContext(UUID conversationId, String newState, Map<String, Object> context) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        conversation.setState(newState);
        if (context != null) {
            Map<String, Object> existingContext = conversation.getContext();
            if (existingContext == null) {
                existingContext = new HashMap<>();
            }
            existingContext.putAll(context);
            conversation.setContext(existingContext);
        }

        return conversationRepository.save(conversation);
    }

    /**
     * Request human handoff
     */
    @Transactional
    public Conversation requestHandoff(UUID conversationId, String reason) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        conversation.handOff(reason);
        return conversationRepository.save(conversation);
    }

    /**
     * Get recent messages for a conversation
     */
    public List<ConversationMessage> getRecentMessages(UUID conversationId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<ConversationMessage> messages = messageRepository.findRecentMessages(conversationId, pageable);
        // Reverse to get chronological order
        List<ConversationMessage> reversed = new java.util.ArrayList<>(messages);
        java.util.Collections.reverse(reversed);
        return reversed;
    }

    /**
     * Get all messages for a conversation
     */
    public List<ConversationMessage> getAllMessages(UUID conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    /**
     * Get active conversations for a business
     */
    public List<Conversation> getActiveConversations(UUID businessId) {
        return conversationRepository.findByBusinessIdAndIsActiveTrueOrderByLastMessageAtDesc(businessId);
    }

    /**
     * Get conversations requiring handoff
     */
    public Page<Conversation> getHandoffConversations(UUID businessId, Pageable pageable) {
        return conversationRepository.findByBusinessIdAndIsHandedOffTrue(businessId, pageable);
    }

    /**
     * Get conversation by ID
     */
    public Optional<Conversation> getConversation(UUID conversationId) {
        return conversationRepository.findById(conversationId);
    }

    /**
     * Find stale conversations that haven't had activity
     */
    public List<Conversation> findStaleConversations(UUID businessId, int hoursInactive) {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(hoursInactive);
        return conversationRepository.findStaleConversations(businessId, cutoff);
    }

    /**
     * Mark conversation as abandoned
     */
    @Transactional
    public Conversation markAsAbandoned(UUID conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        conversation.markAsAbandoned();
        return conversationRepository.save(conversation);
    }

    /**
     * Mark conversation as converted
     */
    @Transactional
    public Conversation markAsConverted(UUID conversationId, UUID orderId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        conversation.markAsConverted(orderId);
        return conversationRepository.save(conversation);
    }

    /**
     * Extract text content from WhatsApp message
     */
    private String extractMessageContent(WhatsAppWebhookPayload.Message message) {
        if (message == null) {
            return "";
        }

        String type = message.getType();
        if (type == null) {
            type = "text";
        }

        return switch (type) {
            case "text" -> message.getText() != null ? message.getText().getBody() : "";
            case "image" -> message.getImage() != null && message.getImage().getCaption() != null
                    ? message.getImage().getCaption()
                    : "[Image]";
            case "audio" -> "[Audio message]";
            case "document" -> message.getDocument() != null && message.getDocument().getFilename() != null
                    ? "[Document: " + message.getDocument().getFilename() + "]"
                    : "[Document]";
            case "interactive" -> extractInteractiveContent(message.getInteractive());
            case "button" -> message.getButton() != null ? message.getButton().getText() : "[Button]";
            default -> "[Unsupported message type: " + type + "]";
        };
    }

    private String extractInteractiveContent(WhatsAppWebhookPayload.InteractiveMessage interactive) {
        if (interactive == null) {
            return "[Interactive]";
        }

        if (interactive.getButtonReply() != null) {
            return interactive.getButtonReply().getTitle();
        }

        if (interactive.getListReply() != null) {
            return interactive.getListReply().getTitle();
        }

        return "[Interactive]";
    }
}
