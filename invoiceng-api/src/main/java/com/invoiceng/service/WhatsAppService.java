package com.invoiceng.service;

import com.invoiceng.config.WhatsAppConfig;
import com.invoiceng.dto.whatsapp.WhatsAppSendRequest;
import com.invoiceng.dto.whatsapp.WhatsAppSendResponse;
import com.invoiceng.entity.User;
import com.invoiceng.exception.WhatsAppException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {

    private final WhatsAppConfig whatsAppConfig;
    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder
                .baseUrl(whatsAppConfig.getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Send a text message via WhatsApp
     */
    public WhatsAppSendResponse sendTextMessage(String phoneNumberId, String accessToken, String recipientPhone, String text) {
        WhatsAppSendRequest request = WhatsAppSendRequest.builder()
                .to(recipientPhone)
                .type("text")
                .text(WhatsAppSendRequest.TextContent.builder()
                        .body(text)
                        .previewUrl(true)
                        .build())
                .build();

        return sendMessage(phoneNumberId, accessToken, request);
    }

    /**
     * Send an image message via WhatsApp
     */
    public WhatsAppSendResponse sendImageMessage(String phoneNumberId, String accessToken, String recipientPhone, String imageUrl, String caption) {
        WhatsAppSendRequest request = WhatsAppSendRequest.builder()
                .to(recipientPhone)
                .type("image")
                .image(WhatsAppSendRequest.ImageContent.builder()
                        .link(imageUrl)
                        .caption(caption)
                        .build())
                .build();

        return sendMessage(phoneNumberId, accessToken, request);
    }

    /**
     * Send a message with interactive buttons
     */
    public WhatsAppSendResponse sendButtonMessage(String phoneNumberId, String accessToken, String recipientPhone, String bodyText, List<Map<String, String>> buttons) {
        List<WhatsAppSendRequest.InteractiveContent.Action.Button> buttonList = buttons.stream()
                .map(b -> WhatsAppSendRequest.InteractiveContent.Action.Button.builder()
                        .type("reply")
                        .reply(WhatsAppSendRequest.InteractiveContent.Action.Button.Reply.builder()
                                .id(b.get("id"))
                                .title(b.get("title"))
                                .build())
                        .build())
                .toList();

        WhatsAppSendRequest request = WhatsAppSendRequest.builder()
                .to(recipientPhone)
                .type("interactive")
                .interactive(WhatsAppSendRequest.InteractiveContent.builder()
                        .type("button")
                        .body(WhatsAppSendRequest.InteractiveContent.Body.builder()
                                .text(bodyText)
                                .build())
                        .action(WhatsAppSendRequest.InteractiveContent.Action.builder()
                                .buttons(buttonList)
                                .build())
                        .build())
                .build();

        return sendMessage(phoneNumberId, accessToken, request);
    }

    /**
     * Send a product info message
     */
    public WhatsAppSendResponse sendProductMessage(String phoneNumberId, String accessToken, String recipientPhone,
                                                    String productName, String description, BigDecimal price, String imageUrl) {
        String message = String.format(
                "*%s*\n\n%s\n\n💰 Price: ₦%,d\n\nReply to order!",
                productName,
                description != null ? description : "",
                price.intValue()
        );

        if (imageUrl != null && !imageUrl.isEmpty()) {
            return sendImageMessage(phoneNumberId, accessToken, recipientPhone, imageUrl, message);
        }

        return sendTextMessage(phoneNumberId, accessToken, recipientPhone, message);
    }

    /**
     * Send payment link message
     */
    public WhatsAppSendResponse sendPaymentLinkMessage(String phoneNumberId, String accessToken, String recipientPhone,
                                                        String orderSummary, BigDecimal subtotal, BigDecimal deliveryFee,
                                                        BigDecimal total, String paymentLink) {
        String message = String.format("""
                ✅ *Order Summary*

                %s

                📦 Subtotal: ₦%,d
                🚚 Delivery: ₦%,d
                ━━━━━━━━━━━━━━━
                💰 *Total: ₦%,d*

                🔗 *Pay securely here:*
                %s

                ⏰ Link expires in 24 hours

                Reply "PAID" once payment is complete!""",
                orderSummary,
                subtotal.intValue(),
                deliveryFee.intValue(),
                total.intValue(),
                paymentLink
        );

        return sendTextMessage(phoneNumberId, accessToken, recipientPhone, message);
    }

    /**
     * Send order confirmation message
     */
    public WhatsAppSendResponse sendOrderConfirmation(String phoneNumberId, String accessToken, String recipientPhone,
                                                       String customerName, String orderNumber, String orderItems,
                                                       String deliveryAddress, String dispatchTime) {
        String message = String.format("""
                🎉 *Payment Received!*

                Thank you, %s! Your order has been confirmed.

                📋 *Order #%s*
                %s

                📍 *Delivery Address:*
                %s

                🚚 We'll dispatch your order within %s.
                You'll receive a notification when it's on the way!

                Thank you for shopping with us! 💚""",
                customerName,
                orderNumber,
                orderItems,
                deliveryAddress,
                dispatchTime
        );

        return sendTextMessage(phoneNumberId, accessToken, recipientPhone, message);
    }

    /**
     * Send welcome/greeting message
     */
    public WhatsAppSendResponse sendGreeting(String phoneNumberId, String accessToken, String recipientPhone,
                                              String businessName, String agentName) {
        String message = String.format(
                "Hello! 👋 Welcome to %s!\n\nI'm %s, your assistant. How can I help you today?",
                businessName,
                agentName != null ? agentName : businessName
        );

        return sendTextMessage(phoneNumberId, accessToken, recipientPhone, message);
    }

    /**
     * Send a generic message request
     */
    public WhatsAppSendResponse sendMessage(String phoneNumberId, String accessToken, WhatsAppSendRequest request) {
        try {
            log.info("Sending WhatsApp message to: {}", request.getTo());

            WhatsAppSendResponse response = webClient.post()
                    .uri("/{version}/{phoneNumberId}/messages", whatsAppConfig.getApiVersion(), phoneNumberId)
                    .header("Authorization", "Bearer " + accessToken)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(WhatsAppSendResponse.class)
                    .block();

            log.info("WhatsApp message sent successfully, messageId: {}",
                    response != null ? response.getFirstMessageId() : "unknown");

            return response;

        } catch (WebClientResponseException e) {
            log.error("WhatsApp API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new WhatsAppException("Failed to send WhatsApp message: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message", e);
            throw new WhatsAppException("Failed to send WhatsApp message", e);
        }
    }

    /**
     * Mark a message as read
     */
    public void markAsRead(String phoneNumberId, String accessToken, String messageId) {
        try {
            Map<String, Object> request = Map.of(
                    "messaging_product", "whatsapp",
                    "status", "read",
                    "message_id", messageId
            );

            webClient.post()
                    .uri("/{version}/{phoneNumberId}/messages", whatsAppConfig.getApiVersion(), phoneNumberId)
                    .header("Authorization", "Bearer " + accessToken)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.debug("Marked message {} as read", messageId);

        } catch (Exception e) {
            log.warn("Failed to mark message as read: {}", e.getMessage());
        }
    }

    /**
     * Get the access token for a business (either from user or default config)
     */
    public String getAccessToken(User business) {
        if (business.getWhatsappAccessToken() != null && !business.getWhatsappAccessToken().isEmpty()) {
            return business.getWhatsappAccessToken();
        }
        return whatsAppConfig.getAccessToken();
    }

    /**
     * Get the phone number ID for a business (either from user or default config)
     */
    public String getPhoneNumberId(User business) {
        if (business.getWhatsappPhoneNumberId() != null && !business.getWhatsappPhoneNumberId().isEmpty()) {
            return business.getWhatsappPhoneNumberId();
        }
        return whatsAppConfig.getPhoneNumberId();
    }
}
