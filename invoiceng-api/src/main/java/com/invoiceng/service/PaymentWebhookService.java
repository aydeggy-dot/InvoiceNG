package com.invoiceng.service;

import com.invoiceng.dto.paystack.PaystackWebhookEvent;
import com.invoiceng.entity.Conversation;
import com.invoiceng.entity.ConversationState;
import com.invoiceng.entity.WhatsAppOrder;
import com.invoiceng.repository.ConversationRepository;
import com.invoiceng.repository.WhatsAppOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookService {

    private final WhatsAppOrderRepository orderRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationService conversationService;
    private final WhatsAppService whatsAppService;

    /**
     * Handle successful payment from Paystack webhook
     */
    @Transactional
    public void handlePaymentSuccess(PaystackWebhookEvent event) {
        PaystackWebhookEvent.PaystackData data = event.getData();

        if (data == null || data.getReference() == null) {
            log.warn("Payment success event missing data or reference");
            return;
        }

        String reference = data.getReference();
        String orderNumber = data.getOrderNumber();

        log.info("Processing payment success for reference: {}, orderNumber: {}", reference, orderNumber);

        // Find the order by order number (reference format is WA-ORDER_NUMBER)
        Optional<WhatsAppOrder> orderOpt = orderRepository.findByOrderNumber(orderNumber);

        if (orderOpt.isEmpty()) {
            // Try finding by payment reference directly
            orderOpt = orderRepository.findByPaymentReference(reference);
        }

        if (orderOpt.isEmpty()) {
            log.warn("No order found for payment reference: {} or order number: {}", reference, orderNumber);
            return;
        }

        WhatsAppOrder order = orderOpt.get();

        // Check if already paid (idempotency)
        if ("paid".equals(order.getPaymentStatus())) {
            log.info("Order {} already marked as paid, skipping", order.getOrderNumber());
            return;
        }

        // Update order payment status
        String paymentMethod = data.getChannel() != null ? data.getChannel() : "paystack";
        order.markAsPaid(reference, paymentMethod);
        order = orderRepository.save(order);

        log.info("Marked order {} as paid via {}", order.getOrderNumber(), paymentMethod);

        // Update conversation state if linked
        if (order.getConversationId() != null) {
            Optional<Conversation> conversationOpt = conversationRepository.findById(order.getConversationId());

            if (conversationOpt.isPresent()) {
                Conversation conversation = conversationOpt.get();
                conversationService.updateState(conversation.getId(), ConversationState.COMPLETED.getValue());

                log.info("Updated conversation {} state to COMPLETED", conversation.getId());
            }
        }

        // Send WhatsApp payment confirmation
        sendPaymentConfirmation(order, data);
    }

    /**
     * Send payment confirmation message via WhatsApp
     */
    private void sendPaymentConfirmation(WhatsAppOrder order, PaystackWebhookEvent.PaystackData paymentData) {
        try {
            String customerPhone = order.getCustomerPhone();

            if (customerPhone == null || customerPhone.isBlank()) {
                log.warn("No customer phone for order {}, cannot send confirmation", order.getOrderNumber());
                return;
            }

            // Build confirmation message
            String message = buildPaymentConfirmationMessage(order, paymentData);

            // Get WhatsApp credentials for the business
            String phoneNumberId = whatsAppService.getPhoneNumberId(order.getBusiness());
            String accessToken = whatsAppService.getAccessToken(order.getBusiness());

            // Send the message
            whatsAppService.sendTextMessage(phoneNumberId, accessToken, customerPhone, message);

            log.info("Sent payment confirmation to {} for order {}", customerPhone, order.getOrderNumber());

            // Save message to conversation if linked
            if (order.getConversationId() != null) {
                Optional<Conversation> conversationOpt = conversationRepository.findById(order.getConversationId());
                if (conversationOpt.isPresent()) {
                    conversationService.saveOutboundMessage(
                            conversationOpt.get(),
                            message,
                            "text",
                            null
                    );
                }
            }

        } catch (Exception e) {
            log.error("Failed to send payment confirmation for order {}: {}",
                    order.getOrderNumber(), e.getMessage(), e);
        }
    }

    private String buildPaymentConfirmationMessage(WhatsAppOrder order, PaystackWebhookEvent.PaystackData paymentData) {
        StringBuilder sb = new StringBuilder();

        sb.append("*Payment Confirmed!* \n\n");
        sb.append("Thank you for your payment. Your order is now being processed.\n\n");

        sb.append("*Order Details:*\n");
        sb.append("Order #: ").append(order.getOrderNumber()).append("\n");
        sb.append("Amount Paid: ₦").append(String.format("%,.2f", order.getTotal())).append("\n");

        if (paymentData.getChannel() != null) {
            String method = formatPaymentMethod(paymentData.getChannel());
            sb.append("Payment Method: ").append(method).append("\n");
        }

        sb.append("\n*Delivery Address:*\n");
        sb.append(order.getDeliveryAddress()).append("\n\n");

        sb.append("We'll notify you when your order is shipped.\n\n");

        sb.append("If you have any questions, please reply to this chat.");

        return sb.toString();
    }

    private String formatPaymentMethod(String channel) {
        return switch (channel.toLowerCase()) {
            case "card" -> "Card Payment";
            case "bank" -> "Bank Payment";
            case "ussd" -> "USSD Payment";
            case "bank_transfer" -> "Bank Transfer";
            case "qr" -> "QR Code Payment";
            case "mobile_money" -> "Mobile Money";
            default -> channel;
        };
    }

    /**
     * Handle order status change and send notification
     */
    @Transactional
    public void sendOrderStatusNotification(WhatsAppOrder order, String newStatus) {
        try {
            String customerPhone = order.getCustomerPhone();

            if (customerPhone == null || customerPhone.isBlank()) {
                log.warn("No customer phone for order {}, cannot send notification", order.getOrderNumber());
                return;
            }

            String message = buildStatusChangeMessage(order, newStatus);

            String phoneNumberId = whatsAppService.getPhoneNumberId(order.getBusiness());
            String accessToken = whatsAppService.getAccessToken(order.getBusiness());

            whatsAppService.sendTextMessage(phoneNumberId, accessToken, customerPhone, message);

            log.info("Sent {} notification to {} for order {}",
                    newStatus, customerPhone, order.getOrderNumber());

            // Save message to conversation if linked
            if (order.getConversationId() != null) {
                Optional<Conversation> conversationOpt = conversationRepository.findById(order.getConversationId());
                if (conversationOpt.isPresent()) {
                    conversationService.saveOutboundMessage(
                            conversationOpt.get(),
                            message,
                            "text",
                            null
                    );
                }
            }

        } catch (Exception e) {
            log.error("Failed to send status notification for order {}: {}",
                    order.getOrderNumber(), e.getMessage(), e);
        }
    }

    private String buildStatusChangeMessage(WhatsAppOrder order, String status) {
        StringBuilder sb = new StringBuilder();

        switch (status.toLowerCase()) {
            case "shipped" -> {
                sb.append("*Your Order Has Been Shipped!* \n\n");
                sb.append("Great news! Your order is on its way.\n\n");
                sb.append("Order #: ").append(order.getOrderNumber()).append("\n");
                if (order.getTrackingNumber() != null) {
                    sb.append("Tracking #: ").append(order.getTrackingNumber()).append("\n");
                }
                sb.append("\n*Delivery Address:*\n");
                sb.append(order.getDeliveryAddress()).append("\n\n");
                sb.append("You'll receive another notification when it's delivered.");
            }
            case "delivered" -> {
                sb.append("*Order Delivered!* \n\n");
                sb.append("Your order has been successfully delivered!\n\n");
                sb.append("Order #: ").append(order.getOrderNumber()).append("\n\n");
                sb.append("Thank you for shopping with us!\n");
                sb.append("We'd love to hear about your experience. ");
                sb.append("Feel free to reply to this chat with any feedback.");
            }
            case "cancelled" -> {
                sb.append("*Order Cancelled*\n\n");
                sb.append("Your order has been cancelled.\n\n");
                sb.append("Order #: ").append(order.getOrderNumber()).append("\n\n");
                sb.append("If you have any questions or if this was a mistake, ");
                sb.append("please reply to this chat and we'll help you out.");
            }
            default -> {
                sb.append("*Order Update*\n\n");
                sb.append("Your order status has been updated to: ").append(status).append("\n\n");
                sb.append("Order #: ").append(order.getOrderNumber()).append("\n\n");
                sb.append("If you have any questions, please reply to this chat.");
            }
        }

        return sb.toString();
    }
}
