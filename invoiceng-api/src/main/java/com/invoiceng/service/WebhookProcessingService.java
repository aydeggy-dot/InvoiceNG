package com.invoiceng.service;

import com.invoiceng.dto.whatsapp.WhatsAppSendResponse;
import com.invoiceng.dto.whatsapp.WhatsAppWebhookPayload;
import com.invoiceng.entity.Conversation;
import com.invoiceng.entity.ConversationMessage;
import com.invoiceng.entity.User;
import com.invoiceng.repository.UserRepository;
import com.invoiceng.service.ai.AISalesAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Handles webhook processing with proper transaction management.
 * Using @Async ensures the processing happens in a managed thread with Spring context.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookProcessingService {

    private final WhatsAppService whatsAppService;
    private final ConversationService conversationService;
    private final UserRepository userRepository;
    private final AISalesAgentService aiSalesAgentService;
    private final WhatsAppOrderService whatsAppOrderService;

    /**
     * Process webhook asynchronously with proper Spring transaction context
     */
    @Async
    public void processWebhookAsync(WhatsAppWebhookPayload payload, String defaultPhoneNumberId) {
        try {
            processWebhook(payload, defaultPhoneNumberId);
        } catch (Exception e) {
            log.error("Error processing webhook", e);
        }
    }

    private void processWebhook(WhatsAppWebhookPayload payload, String defaultPhoneNumberId) {
        if (payload == null || payload.getEntry() == null) {
            log.warn("Received empty webhook payload");
            return;
        }

        for (WhatsAppWebhookPayload.Entry entry : payload.getEntry()) {
            if (entry.getChanges() == null) continue;

            for (WhatsAppWebhookPayload.Change change : entry.getChanges()) {
                if (!"messages".equals(change.getField())) continue;

                processMessagesChange(change.getValue(), defaultPhoneNumberId);
            }
        }
    }

    private void processMessagesChange(WhatsAppWebhookPayload.Value value, String defaultPhoneNumberId) {
        if (value == null) return;

        String phoneNumberId = value.getMetadata() != null ? value.getMetadata().getPhoneNumberId() : null;
        if (phoneNumberId == null) {
            log.warn("No phone number ID in webhook payload");
            return;
        }

        Optional<User> businessOpt = userRepository.findByWhatsappPhoneNumberId(phoneNumberId);
        if (businessOpt.isEmpty()) {
            if (phoneNumberId.equals(defaultPhoneNumberId)) {
                log.debug("Using platform default WhatsApp number, no specific business mapping");
                return;
            }
            log.warn("No business found for WhatsApp phone number ID: {}", phoneNumberId);
            return;
        }

        User business = businessOpt.get();

        if (value.getMessages() != null) {
            for (WhatsAppWebhookPayload.Message message : value.getMessages()) {
                processMessage(business, value, message);
            }
        }

        if (value.getStatuses() != null) {
            for (WhatsAppWebhookPayload.Status status : value.getStatuses()) {
                processStatusUpdate(business, status);
            }
        }
    }

    /**
     * Process message with transactional boundary
     */
    @Transactional
    public void processMessage(User business, WhatsAppWebhookPayload.Value value, WhatsAppWebhookPayload.Message message) {
        try {
            String customerPhone = message.getFrom();
            String messageId = message.getId();

            if (conversationService.isMessageProcessed(messageId)) {
                log.debug("Skipping duplicate message: {}", messageId);
                return;
            }

            String customerName = null;
            String customerWhatsappId = null;
            if (value.getContacts() != null && !value.getContacts().isEmpty()) {
                WhatsAppWebhookPayload.Contact contact = value.getContacts().get(0);
                if (contact.getProfile() != null) {
                    customerName = contact.getProfile().getName();
                }
                customerWhatsappId = contact.getWaId();
            }

            Conversation conversation = conversationService.getOrCreateConversation(
                    business, customerPhone, customerName, customerWhatsappId
            );

            ConversationMessage savedMessage = conversationService.saveInboundMessage(conversation, message);
            if (savedMessage == null) {
                return;
            }

            log.info("Received message from {} for business {}: {}",
                    customerPhone, business.getBusinessName(), savedMessage.getContent());

            String phoneNumberId = whatsAppService.getPhoneNumberId(business);
            String accessToken = whatsAppService.getAccessToken(business);
            whatsAppService.markAsRead(phoneNumberId, accessToken, messageId);

            if (conversation.getIsHandedOff()) {
                log.debug("Conversation {} is handed off, skipping AI response", conversation.getId());
                return;
            }

            List<ConversationMessage> recentMessages = conversationService.getRecentMessages(conversation.getId(), 10);

            // Generate AI response - this is where cart operations happen
            AISalesAgentService.AIResponse aiResult = aiSalesAgentService.generateResponse(
                    conversation, savedMessage.getContent(), recentMessages
            );

            String aiResponse = aiResult.getMessage();

            if (aiResult.isShouldHandoff()) {
                conversationService.requestHandoff(conversation.getId(), aiResult.getHandoffReason());
            }

            if (aiResult.getSuggestedState() != null) {
                conversationService.updateState(conversation.getId(), aiResult.getSuggestedState());
            }

            if (aiResult.isRequiresPaymentLink()) {
                conversation = conversationService.getConversation(conversation.getId()).orElse(conversation);
                WhatsAppOrderService.ConversationOrderResult orderResult =
                        whatsAppOrderService.createOrderFromConversation(conversation);

                if (orderResult.isSuccess()) {
                    log.info("Created order {} with payment link for conversation {}",
                            orderResult.getOrderNumber(), conversation.getId());
                    return;
                } else {
                    aiResponse = aiResponse + "\n\n" + orderResult.getMessage();
                }
            }

            // Send response via WhatsApp
            WhatsAppSendResponse sendResponse = whatsAppService.sendTextMessage(
                    phoneNumberId, accessToken, customerPhone, aiResponse
            );

            if (sendResponse != null) {
                conversationService.saveOutboundMessage(
                        conversation,
                        aiResponse,
                        "text",
                        sendResponse.getFirstMessageId()
                );
            }

        } catch (Exception e) {
            log.error("Error processing message from {}: {}", message.getFrom(), e.getMessage(), e);
        }
    }

    private void processStatusUpdate(User business, WhatsAppWebhookPayload.Status status) {
        log.debug("Message {} status: {} for recipient {}",
                status.getId(), status.getStatus(), status.getRecipientId());
    }
}
