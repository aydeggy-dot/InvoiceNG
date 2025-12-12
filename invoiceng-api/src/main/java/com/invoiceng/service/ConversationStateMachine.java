package com.invoiceng.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceng.dto.CartItem;
import com.invoiceng.dto.OrderContext;
import com.invoiceng.entity.*;
import com.invoiceng.repository.ConversationRepository;
import com.invoiceng.repository.ProductRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Manages conversation state transitions and cart/order operations.
 * This service handles the business logic for progressing through the sales flow.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationStateMachine {

    private final ConversationRepository conversationRepository;
    private final ProductRepository productRepository;
    private final AgentConfigService agentConfigService;
    private final ObjectMapper objectMapper;

    /**
     * Get the current order context from conversation
     */
    public OrderContext getOrderContext(Conversation conversation) {
        try {
            String cart = conversation.getCart();
            if (cart == null || cart.isBlank()) {
                return new OrderContext();
            }
            // Parse JSON string to OrderContext
            return objectMapper.readValue(cart, OrderContext.class);
        } catch (Exception e) {
            log.warn("Failed to parse order context, returning new one: {}", e.getMessage());
            return new OrderContext();
        }
    }

    /**
     * Save order context to conversation
     */
    @Transactional
    public Conversation saveOrderContext(Conversation conversation, OrderContext context) {
        try {
            // Serialize OrderContext to JSON string for storage
            conversation.setCart(objectMapper.writeValueAsString(context));
            return conversationRepository.save(conversation);
        } catch (Exception e) {
            log.error("Failed to save order context: {}", e.getMessage());
            throw new RuntimeException("Failed to save order context", e);
        }
    }

    /**
     * Add product to cart
     */
    @Transactional
    public CartOperationResult addToCart(Conversation conversation, UUID productId, int quantity) {
        ConversationState currentState = ConversationState.fromValue(conversation.getState());

        if (!currentState.canAddToCart()) {
            return CartOperationResult.builder()
                    .success(false)
                    .message("Cannot add items in the current state. Please complete or cancel your current order first.")
                    .build();
        }

        // Find product
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return CartOperationResult.builder()
                    .success(false)
                    .message("Sorry, I couldn't find that product.")
                    .build();
        }

        Product product = productOpt.get();

        // Check if product belongs to this business
        if (!product.getBusiness().getId().equals(conversation.getBusiness().getId())) {
            return CartOperationResult.builder()
                    .success(false)
                    .message("This product is not available from this store.")
                    .build();
        }

        // Check stock if tracking inventory
        if (Boolean.TRUE.equals(product.getTrackInventory())) {
            if (product.getQuantity() == null || product.getQuantity() < quantity) {
                int available = product.getQuantity() != null ? product.getQuantity() : 0;
                return CartOperationResult.builder()
                        .success(false)
                        .message("Sorry, we only have " + available + " of " + product.getName() + " in stock.")
                        .build();
            }
        }

        // Get or create order context
        OrderContext context = getOrderContext(conversation);

        // Create cart item
        CartItem item = CartItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .discountPercent(BigDecimal.ZERO)
                .build();

        context.addItem(item);

        // Update state to ADDING_TO_CART if not already ordering
        if (!currentState.isOrdering()) {
            conversation.setState(ConversationState.ADDING_TO_CART.getValue());
        }

        saveOrderContext(conversation, context);

        return CartOperationResult.builder()
                .success(true)
                .message("Added " + quantity + "x " + product.getName() + " to your cart!")
                .newState(ConversationState.ADDING_TO_CART)
                .orderContext(context)
                .build();
    }

    /**
     * Add product to cart by name (fuzzy matching)
     */
    @Transactional
    public CartOperationResult addToCartByName(Conversation conversation, String productName, int quantity) {
        UUID businessId = conversation.getBusiness().getId();

        // Try exact match first
        List<Product> products = productRepository.findByBusinessIdAndStatusOrderByNameAsc(businessId, "active");

        Product match = null;
        String normalizedInput = productName.toLowerCase().trim();

        // Try exact match
        for (Product p : products) {
            if (p.getName().toLowerCase().equals(normalizedInput)) {
                match = p;
                break;
            }
        }

        // Try contains match
        if (match == null) {
            for (Product p : products) {
                if (p.getName().toLowerCase().contains(normalizedInput) ||
                        normalizedInput.contains(p.getName().toLowerCase())) {
                    match = p;
                    break;
                }
            }
        }

        if (match == null) {
            // Return suggestions
            StringBuilder sb = new StringBuilder("I couldn't find \"" + productName + "\". ");
            if (!products.isEmpty()) {
                sb.append("Here are our available products:\n");
                for (int i = 0; i < Math.min(5, products.size()); i++) {
                    sb.append("- ").append(products.get(i).getName()).append("\n");
                }
            }
            return CartOperationResult.builder()
                    .success(false)
                    .message(sb.toString())
                    .build();
        }

        return addToCart(conversation, match.getId(), quantity);
    }

    /**
     * Update item quantity in cart
     */
    @Transactional
    public CartOperationResult updateCartItemQuantity(Conversation conversation, int itemIndex, int newQuantity) {
        OrderContext context = getOrderContext(conversation);

        if (context.isEmpty() || itemIndex < 0 || itemIndex >= context.getItems().size()) {
            return CartOperationResult.builder()
                    .success(false)
                    .message("Item not found in cart.")
                    .build();
        }

        CartItem item = context.getItems().get(itemIndex);
        String productName = item.getProductName();

        if (newQuantity <= 0) {
            context.removeItem(itemIndex);
            saveOrderContext(conversation, context);

            // If cart is now empty, go back to browsing
            if (context.isEmpty()) {
                conversation.setState(ConversationState.BROWSING.getValue());
                conversationRepository.save(conversation);
            }

            return CartOperationResult.builder()
                    .success(true)
                    .message("Removed " + productName + " from your cart.")
                    .orderContext(context)
                    .build();
        }

        // Check stock
        Optional<Product> productOpt = productRepository.findById(item.getProductId());
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (Boolean.TRUE.equals(product.getTrackInventory())) {
                if (product.getQuantity() == null || product.getQuantity() < newQuantity) {
                    int available = product.getQuantity() != null ? product.getQuantity() : 0;
                    return CartOperationResult.builder()
                            .success(false)
                            .message("Sorry, we only have " + available + " of " + productName + " available.")
                            .build();
                }
            }
        }

        context.updateItemQuantity(itemIndex, newQuantity);
        saveOrderContext(conversation, context);

        return CartOperationResult.builder()
                .success(true)
                .message("Updated " + productName + " quantity to " + newQuantity + ".")
                .orderContext(context)
                .build();
    }

    /**
     * Apply discount to a cart item (for negotiation)
     */
    @Transactional
    public CartOperationResult applyDiscount(Conversation conversation, int itemIndex, BigDecimal discountPercent) {
        AgentConfig config = agentConfigService.getAgentConfigEntity(conversation.getBusiness().getId());

        // Check if negotiation is allowed
        int maxDiscount = 10; // default
        if (config != null && config.getSalesSettings() != null) {
            Boolean negotiationEnabled = (Boolean) config.getSalesSettings().getOrDefault("negotiation_enabled", true);
            if (!negotiationEnabled) {
                return CartOperationResult.builder()
                        .success(false)
                        .message("Sorry, our prices are fixed and we cannot offer discounts.")
                        .build();
            }
            maxDiscount = ((Number) config.getSalesSettings().getOrDefault("max_discount_percent", 10)).intValue();
        }

        if (discountPercent.compareTo(BigDecimal.valueOf(maxDiscount)) > 0) {
            return CartOperationResult.builder()
                    .success(false)
                    .message("Sorry, the maximum discount I can offer is " + maxDiscount + "%.")
                    .build();
        }

        OrderContext context = getOrderContext(conversation);

        if (context.isEmpty() || itemIndex < 0 || itemIndex >= context.getItems().size()) {
            return CartOperationResult.builder()
                    .success(false)
                    .message("Item not found in cart.")
                    .build();
        }

        CartItem item = context.getItems().get(itemIndex);
        item.setDiscountPercent(discountPercent);
        item.calculateTotals();
        context.recalculateTotals();

        saveOrderContext(conversation, context);

        return CartOperationResult.builder()
                .success(true)
                .message("Applied " + discountPercent + "% discount to " + item.getProductName() + "!")
                .orderContext(context)
                .build();
    }

    /**
     * Set delivery address and calculate fee
     */
    @Transactional
    public CartOperationResult setDeliveryAddress(Conversation conversation, String address, String area) {
        OrderContext context = getOrderContext(conversation);

        if (context.isEmpty()) {
            return CartOperationResult.builder()
                    .success(false)
                    .message("Your cart is empty. Please add some items first!")
                    .build();
        }

        context.setDeliveryAddress(address);
        context.setDeliveryArea(area);

        // Calculate delivery fee based on area
        AgentConfig config = agentConfigService.getAgentConfigEntity(conversation.getBusiness().getId());
        BigDecimal deliveryFee = BigDecimal.ZERO;

        if (config != null) {
            if (config.getDeliveryAreas() != null && area != null) {
                // Try to match area for specific fee
                for (Map<String, Object> areaConfig : config.getDeliveryAreas()) {
                    String areaName = (String) areaConfig.get("name");
                    if (areaName != null && areaName.equalsIgnoreCase(area)) {
                        Object fee = areaConfig.get("fee");
                        if (fee instanceof Number) {
                            deliveryFee = BigDecimal.valueOf(((Number) fee).doubleValue());
                        }
                        break;
                    }
                }
            }
            // Fall back to default delivery fee
            if (deliveryFee.compareTo(BigDecimal.ZERO) == 0 && config.getDefaultDeliveryFee() != null) {
                deliveryFee = config.getDefaultDeliveryFee();
            }
        }

        context.setDeliveryFee(deliveryFee);
        context.recalculateTotals();

        // Update state
        conversation.setState(ConversationState.COLLECTING_ADDRESS.getValue());

        saveOrderContext(conversation, context);

        return CartOperationResult.builder()
                .success(true)
                .message("Delivery address set to: " + address)
                .newState(ConversationState.COLLECTING_ADDRESS)
                .orderContext(context)
                .build();
    }

    /**
     * Transition to confirmation state
     */
    @Transactional
    public CartOperationResult prepareForConfirmation(Conversation conversation) {
        OrderContext context = getOrderContext(conversation);

        if (!context.isReadyForConfirmation()) {
            if (context.isEmpty()) {
                return CartOperationResult.builder()
                        .success(false)
                        .message("Your cart is empty. Please add some items first!")
                        .build();
            }
            if (context.getDeliveryAddress() == null || context.getDeliveryAddress().isBlank()) {
                return CartOperationResult.builder()
                        .success(false)
                        .message("Please provide your delivery address first.")
                        .build();
            }
        }

        conversation.setState(ConversationState.CONFIRMING_ORDER.getValue());
        conversationRepository.save(conversation);

        String summary = context.getCartSummary();
        summary += "\n\n*Delivery to:* " + context.getDeliveryAddress();

        return CartOperationResult.builder()
                .success(true)
                .message(summary + "\n\nPlease confirm this order by saying *YES* or *CONFIRM*.")
                .newState(ConversationState.CONFIRMING_ORDER)
                .orderContext(context)
                .build();
    }

    /**
     * Confirm order and mark as ready for payment
     */
    @Transactional
    public CartOperationResult confirmOrder(Conversation conversation) {
        ConversationState currentState = ConversationState.fromValue(conversation.getState());

        if (currentState != ConversationState.CONFIRMING_ORDER) {
            return CartOperationResult.builder()
                    .success(false)
                    .message("Please review your order first before confirming.")
                    .build();
        }

        OrderContext context = getOrderContext(conversation);
        context.setConfirmed(true);

        conversation.setState(ConversationState.AWAITING_PAYMENT.getValue());

        saveOrderContext(conversation, context);

        return CartOperationResult.builder()
                .success(true)
                .message("Order confirmed! I'll send you a payment link shortly.")
                .newState(ConversationState.AWAITING_PAYMENT)
                .orderContext(context)
                .requiresPaymentLink(true)
                .build();
    }

    /**
     * Cancel current order and clear cart
     */
    @Transactional
    public CartOperationResult cancelOrder(Conversation conversation) {
        OrderContext context = getOrderContext(conversation);
        context.clearCart();
        context.setConfirmed(false);
        context.setDeliveryAddress(null);
        context.setDeliveryArea(null);
        context.setDeliveryFee(null);

        conversation.setState(ConversationState.BROWSING.getValue());

        saveOrderContext(conversation, context);

        return CartOperationResult.builder()
                .success(true)
                .message("Your order has been cancelled. Is there anything else I can help you with?")
                .newState(ConversationState.BROWSING)
                .orderContext(context)
                .build();
    }

    /**
     * Complete order after payment received
     */
    @Transactional
    public CartOperationResult completeOrder(Conversation conversation, UUID invoiceId) {
        OrderContext context = getOrderContext(conversation);
        context.setInvoiceId(invoiceId.toString());

        conversation.setState(ConversationState.COMPLETED.getValue());
        conversation.setOrderId(invoiceId);
        conversation.setOutcome("converted");

        saveOrderContext(conversation, context);

        return CartOperationResult.builder()
                .success(true)
                .message("Payment received! Thank you for your order. Your invoice number is " + invoiceId)
                .newState(ConversationState.COMPLETED)
                .orderContext(context)
                .build();
    }

    /**
     * Get cart summary
     */
    public String getCartSummary(Conversation conversation) {
        OrderContext context = getOrderContext(conversation);
        return context.getCartSummary();
    }

    /**
     * Result of cart operation
     */
    @Data
    @Builder
    public static class CartOperationResult {
        private boolean success;
        private String message;
        private ConversationState newState;
        private OrderContext orderContext;
        private boolean requiresPaymentLink;
    }
}
