package com.invoiceng.service;

import com.invoiceng.dto.CartItem;
import com.invoiceng.dto.OrderContext;
import com.invoiceng.dto.request.CreateWhatsAppOrderRequest;
import com.invoiceng.dto.request.UpdateWhatsAppOrderRequest;
import com.invoiceng.dto.response.PaginatedResponse;
import com.invoiceng.dto.response.WhatsAppOrderResponse;
import com.invoiceng.dto.whatsapp.WhatsAppSendResponse;
import com.invoiceng.entity.Conversation;
import com.invoiceng.entity.ConversationState;
import com.invoiceng.entity.User;
import com.invoiceng.entity.WhatsAppOrder;
import com.invoiceng.exception.ResourceNotFoundException;
import com.invoiceng.exception.ValidationException;
import com.invoiceng.repository.UserRepository;
import com.invoiceng.repository.WhatsAppOrderRepository;
import com.invoiceng.util.PhoneNumberFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WhatsAppOrderService {

    private final WhatsAppOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PhoneNumberFormatter phoneFormatter;
    private final ConversationStateMachine stateMachine;
    private final ConversationService conversationService;
    private final PaystackService paystackService;
    private final WhatsAppService whatsAppService;

    @org.springframework.context.annotation.Lazy
    @org.springframework.beans.factory.annotation.Autowired
    private PaymentWebhookService paymentWebhookService;

    private static final AtomicInteger orderCounter = new AtomicInteger(1);

    public PaginatedResponse<WhatsAppOrderResponse> listOrders(
            UUID businessId,
            String paymentStatus,
            String fulfillmentStatus,
            int page,
            int limit,
            String sortBy,
            String sortOrder
    ) {
        Pageable pageable = createPageable(page, limit, sortBy, sortOrder);

        Page<WhatsAppOrder> orderPage;
        if (paymentStatus != null && !paymentStatus.isBlank()) {
            orderPage = orderRepository.findByBusinessIdAndPaymentStatus(businessId, paymentStatus, pageable);
        } else if (fulfillmentStatus != null && !fulfillmentStatus.isBlank()) {
            orderPage = orderRepository.findByBusinessIdAndFulfillmentStatus(businessId, fulfillmentStatus, pageable);
        } else {
            orderPage = orderRepository.findByBusinessId(businessId, pageable);
        }

        return PaginatedResponse.fromPage(orderPage, WhatsAppOrderResponse::fromEntityBasic);
    }

    public WhatsAppOrderResponse getOrder(UUID orderId, UUID businessId) {
        WhatsAppOrder order = orderRepository.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        return WhatsAppOrderResponse.fromEntity(order);
    }

    public WhatsAppOrderResponse getOrderByNumber(String orderNumber) {
        WhatsAppOrder order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        return WhatsAppOrderResponse.fromEntity(order);
    }

    @Transactional
    public WhatsAppOrderResponse createOrder(CreateWhatsAppOrderRequest request, UUID businessId) {
        User business = userRepository.getReferenceById(businessId);

        String formattedPhone = phoneFormatter.formatToInternational(request.getCustomerPhone());

        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        List<Map<String, Object>> orderItems = new ArrayList<>();

        for (CreateWhatsAppOrderRequest.OrderItemRequest item : request.getItems()) {
            BigDecimal itemTotal = item.getTotal() != null ? item.getTotal() :
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("productId", item.getProductId() != null ? item.getProductId().toString() : null);
            orderItem.put("variantId", item.getVariantId() != null ? item.getVariantId().toString() : null);
            orderItem.put("name", item.getName());
            orderItem.put("quantity", item.getQuantity());
            orderItem.put("price", item.getPrice());
            orderItem.put("total", itemTotal);
            orderItems.add(orderItem);
        }

        BigDecimal deliveryFee = request.getDeliveryFee() != null ? request.getDeliveryFee() : BigDecimal.ZERO;
        BigDecimal discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(deliveryFee).subtract(discountAmount);

        WhatsAppOrder order = WhatsAppOrder.builder()
                .business(business)
                .orderNumber(generateOrderNumber())
                .customerName(request.getCustomerName())
                .customerPhone(formattedPhone)
                .customerEmail(request.getCustomerEmail())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryArea(request.getDeliveryArea())
                .deliveryFee(deliveryFee)
                .deliveryNotes(request.getDeliveryNotes())
                .items(orderItems)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .discountReason(request.getDiscountReason())
                .total(total)
                .internalNotes(request.getInternalNotes())
                .source("whatsapp")
                .build();

        order = orderRepository.save(order);
        log.info("Created WhatsApp order {} for business {}", order.getOrderNumber(), businessId);

        return WhatsAppOrderResponse.fromEntity(order);
    }

    @Transactional
    public WhatsAppOrderResponse updateOrder(UUID orderId, UpdateWhatsAppOrderRequest request, UUID businessId) {
        WhatsAppOrder order = orderRepository.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (request.getCustomerName() != null) {
            order.setCustomerName(request.getCustomerName());
        }
        if (request.getCustomerPhone() != null) {
            order.setCustomerPhone(phoneFormatter.formatToInternational(request.getCustomerPhone()));
        }
        if (request.getCustomerEmail() != null) {
            order.setCustomerEmail(request.getCustomerEmail());
        }
        if (request.getDeliveryAddress() != null) {
            order.setDeliveryAddress(request.getDeliveryAddress());
        }
        if (request.getDeliveryArea() != null) {
            order.setDeliveryArea(request.getDeliveryArea());
        }
        if (request.getDeliveryFee() != null) {
            order.setDeliveryFee(request.getDeliveryFee());
            recalculateTotal(order);
        }
        if (request.getDeliveryNotes() != null) {
            order.setDeliveryNotes(request.getDeliveryNotes());
        }
        if (request.getPaymentStatus() != null) {
            updatePaymentStatus(order, request.getPaymentStatus(), request.getPaymentReference(), request.getPaymentMethod());
        }
        if (request.getFulfillmentStatus() != null) {
            updateFulfillmentStatus(order, request.getFulfillmentStatus(), request.getTrackingNumber());
        }
        if (request.getInternalNotes() != null) {
            order.setInternalNotes(request.getInternalNotes());
        }

        order = orderRepository.save(order);
        log.info("Updated order {}", order.getOrderNumber());

        return WhatsAppOrderResponse.fromEntity(order);
    }

    @Transactional
    public WhatsAppOrderResponse markAsPaid(UUID orderId, UUID businessId, String paymentReference, String paymentMethod) {
        WhatsAppOrder order = orderRepository.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if ("paid".equals(order.getPaymentStatus())) {
            throw new ValidationException("Order is already marked as paid");
        }

        order.markAsPaid(paymentReference, paymentMethod);
        order = orderRepository.save(order);
        log.info("Marked order {} as paid", order.getOrderNumber());

        return WhatsAppOrderResponse.fromEntity(order);
    }

    @Transactional
    public WhatsAppOrderResponse markAsShipped(UUID orderId, UUID businessId, String trackingNumber) {
        WhatsAppOrder order = orderRepository.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.markAsShipped(trackingNumber);
        order = orderRepository.save(order);
        log.info("Marked order {} as shipped", order.getOrderNumber());

        // Send WhatsApp notification
        paymentWebhookService.sendOrderStatusNotification(order, "shipped");

        return WhatsAppOrderResponse.fromEntity(order);
    }

    @Transactional
    public WhatsAppOrderResponse markAsDelivered(UUID orderId, UUID businessId) {
        WhatsAppOrder order = orderRepository.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.markAsDelivered();
        order = orderRepository.save(order);
        log.info("Marked order {} as delivered", order.getOrderNumber());

        // Send WhatsApp notification
        paymentWebhookService.sendOrderStatusNotification(order, "delivered");

        return WhatsAppOrderResponse.fromEntity(order);
    }

    @Transactional
    public WhatsAppOrderResponse cancelOrder(UUID orderId, UUID businessId) {
        WhatsAppOrder order = orderRepository.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if ("delivered".equals(order.getFulfillmentStatus())) {
            throw new ValidationException("Cannot cancel a delivered order");
        }

        order.cancel();
        order = orderRepository.save(order);
        log.info("Cancelled order {}", order.getOrderNumber());

        // Send WhatsApp notification
        paymentWebhookService.sendOrderStatusNotification(order, "cancelled");

        return WhatsAppOrderResponse.fromEntity(order);
    }

    public long countOrdersSince(UUID businessId, LocalDateTime since) {
        return orderRepository.countOrdersSince(businessId, since);
    }

    public long countPaidOrdersSince(UUID businessId, LocalDateTime since) {
        return orderRepository.countPaidOrdersSince(businessId, since);
    }

    public BigDecimal sumRevenueSince(UUID businessId, LocalDateTime since) {
        return orderRepository.sumRevenueSince(businessId, since);
    }

    private String generateOrderNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int counter = orderCounter.getAndIncrement();
        return String.format("WA-%s-%04d", datePart, counter);
    }

    private void recalculateTotal(WhatsAppOrder order) {
        BigDecimal total = order.getSubtotal()
                .add(order.getDeliveryFee())
                .subtract(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
        order.setTotal(total);
    }

    private void updatePaymentStatus(WhatsAppOrder order, String status, String reference, String method) {
        order.setPaymentStatus(status);
        if ("paid".equals(status)) {
            order.setPaidAt(LocalDateTime.now());
            if (reference != null) {
                order.setPaymentReference(reference);
            }
            if (method != null) {
                order.setPaymentMethod(method);
            }
        }
    }

    private void updateFulfillmentStatus(WhatsAppOrder order, String status, String trackingNumber) {
        order.setFulfillmentStatus(status);
        if ("shipped".equals(status)) {
            order.setShippedAt(LocalDateTime.now());
            if (trackingNumber != null) {
                order.setTrackingNumber(trackingNumber);
            }
        } else if ("delivered".equals(status)) {
            order.setDeliveredAt(LocalDateTime.now());
        }
    }

    private Pageable createPageable(int page, int limit, String sortBy, String sortOrder) {
        page = Math.max(1, page) - 1;
        limit = Math.min(Math.max(1, limit), 100);

        String sortField = switch (sortBy != null ? sortBy.toLowerCase() : "createdat") {
            case "ordernumber" -> "orderNumber";
            case "total" -> "total";
            case "paymentstatus" -> "paymentStatus";
            case "fulfillmentstatus" -> "fulfillmentStatus";
            default -> "createdAt";
        };

        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(page, limit, Sort.by(direction, sortField));
    }

    /**
     * Create an order from a confirmed WhatsApp conversation and send payment link.
     * This is called when the AI confirms an order during conversation.
     */
    @Transactional
    public ConversationOrderResult createOrderFromConversation(Conversation conversation) {
        OrderContext context = stateMachine.getOrderContext(conversation);
        User business = conversation.getBusiness();

        if (context.isEmpty()) {
            return ConversationOrderResult.builder()
                    .success(false)
                    .message("Cannot create order - cart is empty")
                    .build();
        }

        if (!context.isConfirmed()) {
            return ConversationOrderResult.builder()
                    .success(false)
                    .message("Order has not been confirmed yet")
                    .build();
        }

        try {
            // Convert cart items to order items
            List<Map<String, Object>> orderItems = new ArrayList<>();
            for (CartItem cartItem : context.getItems()) {
                Map<String, Object> item = new HashMap<>();
                item.put("productId", cartItem.getProductId() != null ? cartItem.getProductId().toString() : null);
                item.put("name", cartItem.getProductName());
                item.put("quantity", cartItem.getQuantity());
                item.put("price", cartItem.getUnitPrice());
                item.put("discount", cartItem.getDiscountPercent());
                item.put("finalPrice", cartItem.getFinalUnitPrice());
                item.put("total", cartItem.getLineTotal());
                orderItems.add(item);
            }

            // Create WhatsApp order
            String orderNumber = generateOrderNumber();
            WhatsAppOrder order = WhatsAppOrder.builder()
                    .business(business)
                    .orderNumber(orderNumber)
                    .customerName(conversation.getCustomerName() != null ?
                            conversation.getCustomerName() : "WhatsApp Customer")
                    .customerPhone(conversation.getCustomerPhone())
                    .deliveryAddress(context.getDeliveryAddress())
                    .deliveryArea(context.getDeliveryArea())
                    .deliveryFee(context.getDeliveryFee() != null ? context.getDeliveryFee() : BigDecimal.ZERO)
                    .deliveryNotes(context.getDeliveryNotes())
                    .items(orderItems)
                    .subtotal(context.getSubtotal())
                    .discountAmount(context.getTotalDiscount())
                    .total(context.getGrandTotal())
                    .conversationId(conversation.getId())
                    .source("whatsapp_ai")
                    .build();

            order = orderRepository.save(order);

            // Generate payment link using Paystack
            String paymentLink = null;
            try {
                String paymentRef = "WA-" + order.getOrderNumber();
                String customerEmail = "whatsapp@" + business.getId() + ".invoiceng.com"; // placeholder

                PaystackService.PaystackInitResponse paystackResponse = paystackService.initializeTransaction(
                        paymentRef,
                        context.getGrandTotal(),
                        customerEmail,
                        conversation.getCustomerName() != null ? conversation.getCustomerName() : "Customer",
                        null // invoice ID not used for WhatsApp orders
                );

                paymentLink = paystackResponse.getAuthorizationUrl();
                order.setPaymentLink(paymentLink);
                order = orderRepository.save(order);
            } catch (Exception e) {
                log.warn("Failed to create Paystack payment link: {}", e.getMessage());
                // Continue without payment link - can be generated later
                paymentLink = "Payment link unavailable - contact us for payment options";
            }

            // Update order context
            context.setInvoiceId(order.getId().toString());
            context.setPaymentLink(paymentLink);
            stateMachine.saveOrderContext(conversation, context);

            // Update conversation state
            conversationService.updateStateAndContext(
                    conversation.getId(),
                    ConversationState.AWAITING_PAYMENT.getValue(),
                    null
            );
            conversation.setOrderId(order.getId());

            // Build and send payment message
            String paymentMessage = buildPaymentMessage(order, context, paymentLink);
            String phoneNumberId = whatsAppService.getPhoneNumberId(business);
            String accessToken = whatsAppService.getAccessToken(business);

            WhatsAppSendResponse sendResponse = whatsAppService.sendTextMessage(
                    phoneNumberId,
                    accessToken,
                    conversation.getCustomerPhone(),
                    paymentMessage
            );

            if (sendResponse != null) {
                conversationService.saveOutboundMessage(
                        conversation,
                        paymentMessage,
                        "text",
                        sendResponse.getFirstMessageId()
                );
            }

            log.info("Created order {} from conversation {} and sent payment link",
                    order.getOrderNumber(), conversation.getId());

            return ConversationOrderResult.builder()
                    .success(true)
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .paymentLink(paymentLink)
                    .message(paymentMessage)
                    .build();

        } catch (Exception e) {
            log.error("Failed to create order from conversation {}: {}",
                    conversation.getId(), e.getMessage(), e);
            return ConversationOrderResult.builder()
                    .success(false)
                    .message("Sorry, there was an error processing your order. Please try again.")
                    .build();
        }
    }

    private String buildPaymentMessage(WhatsAppOrder order, OrderContext context, String paymentLink) {
        StringBuilder sb = new StringBuilder();

        sb.append("*Order Confirmed!*\n\n");
        sb.append("Order #: ").append(order.getOrderNumber()).append("\n\n");
        sb.append(context.getCartSummary()).append("\n\n");

        if (context.getDeliveryAddress() != null) {
            sb.append("*Delivery to:* ").append(context.getDeliveryAddress()).append("\n\n");
        }

        if (paymentLink != null && paymentLink.startsWith("http")) {
            sb.append("Please complete your payment using this secure link:\n");
            sb.append(paymentLink).append("\n\n");
            sb.append("You can pay with card or bank transfer. ");
        } else {
            sb.append("Please contact us for payment options.\n\n");
        }

        sb.append("We'll start preparing your order once payment is confirmed!");

        return sb.toString();
    }

    /**
     * Result of creating an order from conversation
     */
    @lombok.Data
    @lombok.Builder
    public static class ConversationOrderResult {
        private boolean success;
        private UUID orderId;
        private String orderNumber;
        private String paymentLink;
        private String message;
    }
}
