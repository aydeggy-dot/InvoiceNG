package com.invoiceng.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceng.dto.OrderContext;
import com.invoiceng.entity.*;
import com.invoiceng.repository.ProductRepository;
import com.invoiceng.repository.UserRepository;
import com.invoiceng.service.AgentConfigService;
import com.invoiceng.service.ConversationStateMachine;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AISalesAgentService {

    private final ClaudeService claudeService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AgentConfigService agentConfigService;
    private final ConversationStateMachine stateMachine;
    private final ObjectMapper objectMapper;

    // Patterns for extracting actions from AI response
    private static final Pattern ADD_TO_CART_PATTERN = Pattern.compile(
            "\\[ADD_TO_CART:\\s*\"([^\"]+)\"\\s*,\\s*(\\d+)\\]",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern SET_ADDRESS_PATTERN = Pattern.compile(
            "\\[SET_ADDRESS:\\s*\"([^\"]+)\"\\]",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern CONFIRM_ORDER_PATTERN = Pattern.compile(
            "\\[CONFIRM_ORDER\\]",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern CANCEL_ORDER_PATTERN = Pattern.compile(
            "\\[CANCEL_ORDER\\]",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern APPLY_DISCOUNT_PATTERN = Pattern.compile(
            "\\[APPLY_DISCOUNT:\\s*(\\d+)%?\\]",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern HANDOFF_PATTERN = Pattern.compile(
            "\\[HANDOFF(?::\\s*\"([^\"]+)\")?\\]",
            Pattern.CASE_INSENSITIVE
    );

    public AIResponse generateResponse(Conversation conversation, String customerMessage, List<ConversationMessage> recentMessages) {
        // Re-fetch business entity to avoid LazyInitializationException in async context
        UUID businessId = conversation.getBusiness().getId();
        User business = userRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found: " + businessId));
        AgentConfig config = agentConfigService.getAgentConfigEntity(businessId);
        ConversationState currentState = ConversationState.fromValue(conversation.getState());
        OrderContext orderContext = stateMachine.getOrderContext(conversation);

        // Check if Claude is configured
        if (!claudeService.isConfigured()) {
            log.warn("Claude API not configured, using fallback response");
            return generateFallbackResponse(conversation, businessId, customerMessage, config, orderContext);
        }

        try {
            // Build context for Claude with state and cart info
            String systemPrompt = buildSystemPrompt(business, config, currentState, orderContext);
            List<ClaudeService.Message> messages = buildMessageHistory(recentMessages, customerMessage);

            // Get AI response
            String aiResponse = claudeService.chat(systemPrompt, messages);

            if (aiResponse == null || aiResponse.isBlank()) {
                return generateFallbackResponse(conversation, businessId, customerMessage, config, orderContext);
            }

            // Parse response and execute any embedded actions
            return parseAndExecuteResponse(aiResponse, conversation, orderContext);

        } catch (Exception e) {
            log.error("Error generating AI response: {}", e.getMessage(), e);
            return generateFallbackResponse(conversation, businessId, customerMessage, config, orderContext);
        }
    }

    private String buildSystemPrompt(User business, AgentConfig config, ConversationState state, OrderContext orderContext) {
        StringBuilder prompt = new StringBuilder();

        // Agent identity and personality
        String agentName = config != null && config.getAgentName() != null ?
                config.getAgentName() : "Ayo";
        String businessName = business.getBusinessName() != null ?
                business.getBusinessName() : "our store";

        prompt.append("You are ").append(agentName).append(", a friendly WhatsApp sales assistant for ")
                .append(businessName).append(" in Nigeria. You're warm, helpful, and great at closing sales.\n\n");

        // Core behavior rules - CRITICAL
        prompt.append("CRITICAL RULES:\n");
        prompt.append("1. Keep responses SHORT (1-2 sentences max). WhatsApp users hate long messages.\n");
        prompt.append("2. Be conversational and warm - use light Nigerian English flavor.\n");
        prompt.append("3. Always guide toward a purchase - ask if they want to order, offer help.\n");
        prompt.append("4. NEVER invent products or prices not in your catalog.\n");
        prompt.append("5. Use emojis sparingly (1-2 max per message).\n");
        prompt.append("6. NEVER invent or fabricate bank account details, payment info, or any business information not provided below.\n");
        prompt.append("7. For payments: ONLY tell customers 'I'll send you a payment link shortly' - NEVER provide manual bank transfer details.\n\n");

        // Available products - IMPORTANT
        List<Product> products = productRepository.findByBusinessIdAndStatusOrderByNameAsc(business.getId(), "active");
        if (!products.isEmpty()) {
            prompt.append("YOUR PRODUCTS:\n");
            for (Product p : products) {
                prompt.append("• ").append(p.getName());
                prompt.append(" - ₦").append(p.getPrice().setScale(0, RoundingMode.HALF_UP).toPlainString());
                if (Boolean.TRUE.equals(p.getTrackInventory()) && p.getQuantity() != null) {
                    if (p.getQuantity() <= 0) {
                        prompt.append(" [SOLD OUT]");
                    } else if (p.getQuantity() < 5) {
                        prompt.append(" [Only ").append(p.getQuantity()).append(" left!]");
                    }
                }
                prompt.append("\n");
            }
            prompt.append("\n");
        }

        // Current order context
        if (!orderContext.isEmpty()) {
            prompt.append("CUSTOMER'S CART: ").append(orderContext.getCartSummary()).append("\n");
            if (orderContext.getDeliveryAddress() != null) {
                prompt.append("Delivery to: ").append(orderContext.getDeliveryAddress()).append("\n");
            }
            prompt.append("\n");
        }

        // Delivery info
        String dispatchTime = config != null && config.getDispatchTime() != null ?
                config.getDispatchTime() : "24-48 hours";
        BigDecimal deliveryFee = config != null && config.getDefaultDeliveryFee() != null ?
                config.getDefaultDeliveryFee() : BigDecimal.valueOf(1500);
        prompt.append("DELIVERY: ₦").append(deliveryFee.setScale(0, RoundingMode.HALF_UP).toPlainString());
        prompt.append(" fee, ships within ").append(dispatchTime).append("\n\n");

        // Action commands
        prompt.append("ACTIONS (include in your response when appropriate):\n");
        prompt.append("[ADD_TO_CART: \"exact product name\", quantity] - when customer wants to buy\n");
        prompt.append("[SET_ADDRESS: \"full address\"] - when customer gives address\n");
        prompt.append("[CONFIRM_ORDER] - when customer says yes/confirm/proceed\n");
        prompt.append("[HANDOFF: \"reason\"] - only for complex issues needing human help\n\n");

        // State-specific guidance
        prompt.append("CURRENT SITUATION: ").append(getStateGuidance(state, orderContext)).append("\n\n");

        // Example responses for quality
        prompt.append("EXAMPLE GOOD RESPONSES:\n");
        prompt.append("• Greeting: \"Hi! 👋 Welcome to ").append(businessName).append("! What can I help you find today?\"\n");
        prompt.append("• Product inquiry: \"Yes! Our [Product] is ₦X. Very popular! Want me to add it to your cart?\"\n");
        prompt.append("• After adding to cart: \"Added! ✓ Anything else, or should we proceed to checkout?\"\n");
        prompt.append("• Asking for address: \"Great! Where should we deliver? Please share your full address.\"\n");

        return prompt.toString();
    }

    private String getStateGuidance(ConversationState state, OrderContext orderContext) {
        switch (state) {
            case GREETING:
                return "Customer just said hi. Greet warmly and ask what they're looking for.";
            case BROWSING:
                return "Customer is browsing. Help them find products, answer questions, encourage purchase.";
            case ADDING_TO_CART:
                return "Customer has items in cart. Ask if they want more or are ready to checkout.";
            case COLLECTING_ADDRESS:
                return "Need delivery address. Ask for their full address with area/city.";
            case CONFIRMING_ORDER:
                return "Waiting for confirmation. Show order summary and ask them to confirm.";
            case AWAITING_PAYMENT:
                return "Payment link was sent. Help with payment questions, encourage completion. NEVER give bank account details - only say 'check the payment link I sent'.";
            default:
                return "Help the customer and guide them toward making a purchase.";
        }
    }

    private List<ClaudeService.Message> buildMessageHistory(List<ConversationMessage> recentMessages, String currentMessage) {
        List<ClaudeService.Message> messages = new ArrayList<>();

        for (ConversationMessage msg : recentMessages) {
            if ("inbound".equals(msg.getDirection())) {
                messages.add(ClaudeService.Message.user(msg.getContent()));
            } else {
                messages.add(ClaudeService.Message.assistant(msg.getContent()));
            }
        }

        messages.add(ClaudeService.Message.user(currentMessage));
        return messages;
    }

    private AIResponse parseAndExecuteResponse(String response, Conversation conversation, OrderContext context) {
        AIResponse.AIResponseBuilder builder = AIResponse.builder()
                .shouldHandoff(false);

        String cleanedResponse = response;
        List<String> actions = new ArrayList<>();

        // Check for ADD_TO_CART action
        Matcher addMatcher = ADD_TO_CART_PATTERN.matcher(response);
        while (addMatcher.find()) {
            String productName = addMatcher.group(1);
            int quantity = Integer.parseInt(addMatcher.group(2));
            actions.add("ADD_TO_CART:" + productName + "," + quantity);

            ConversationStateMachine.CartOperationResult result = stateMachine.addToCartByName(
                    conversation, productName, quantity
            );
            if (!result.isSuccess()) {
                // Include error in response
                cleanedResponse = cleanedResponse.replace(addMatcher.group(), "") + "\n" + result.getMessage();
            }
            context = result.getOrderContext() != null ? result.getOrderContext() : context;
        }
        cleanedResponse = ADD_TO_CART_PATTERN.matcher(cleanedResponse).replaceAll("");

        // Check for SET_ADDRESS action
        Matcher addressMatcher = SET_ADDRESS_PATTERN.matcher(response);
        if (addressMatcher.find()) {
            String address = addressMatcher.group(1);
            actions.add("SET_ADDRESS:" + address);

            ConversationStateMachine.CartOperationResult result = stateMachine.setDeliveryAddress(
                    conversation, address, null
            );
            context = result.getOrderContext() != null ? result.getOrderContext() : context;
            if (result.getNewState() != null) {
                builder.suggestedState(result.getNewState().getValue());
            }
        }
        cleanedResponse = SET_ADDRESS_PATTERN.matcher(cleanedResponse).replaceAll("");

        // Check for CONFIRM_ORDER action
        if (CONFIRM_ORDER_PATTERN.matcher(response).find()) {
            actions.add("CONFIRM_ORDER");
            ConversationStateMachine.CartOperationResult result = stateMachine.confirmOrder(conversation);
            if (result.isRequiresPaymentLink()) {
                builder.requiresPaymentLink(true);
            }
            if (result.getNewState() != null) {
                builder.suggestedState(result.getNewState().getValue());
            }
        }
        cleanedResponse = CONFIRM_ORDER_PATTERN.matcher(cleanedResponse).replaceAll("");

        // Check for CANCEL_ORDER action
        if (CANCEL_ORDER_PATTERN.matcher(response).find()) {
            actions.add("CANCEL_ORDER");
            stateMachine.cancelOrder(conversation);
            builder.suggestedState(ConversationState.BROWSING.getValue());
        }
        cleanedResponse = CANCEL_ORDER_PATTERN.matcher(cleanedResponse).replaceAll("");

        // Check for APPLY_DISCOUNT action
        Matcher discountMatcher = APPLY_DISCOUNT_PATTERN.matcher(response);
        if (discountMatcher.find()) {
            int discount = Integer.parseInt(discountMatcher.group(1));
            actions.add("APPLY_DISCOUNT:" + discount);
            if (!context.isEmpty()) {
                int lastIndex = context.getItems().size() - 1;
                stateMachine.applyDiscount(conversation, lastIndex, BigDecimal.valueOf(discount));
            }
        }
        cleanedResponse = APPLY_DISCOUNT_PATTERN.matcher(cleanedResponse).replaceAll("");

        // Check for HANDOFF action
        Matcher handoffMatcher = HANDOFF_PATTERN.matcher(response);
        if (handoffMatcher.find()) {
            String reason = handoffMatcher.group(1);
            builder.shouldHandoff(true);
            builder.handoffReason(reason != null ? reason : "Customer requested human assistance");
        }
        cleanedResponse = HANDOFF_PATTERN.matcher(cleanedResponse).replaceAll("");

        // Clean up response
        cleanedResponse = cleanedResponse.trim().replaceAll("\\s+", " ");

        builder.message(cleanedResponse);
        builder.executedActions(actions);
        builder.orderContext(context);

        return builder.build();
    }

    private AIResponse generateFallbackResponse(Conversation conversation, UUID businessId, String customerMessage,
                                                 AgentConfig config, OrderContext orderContext) {
        String response;
        ConversationState state = ConversationState.fromValue(conversation.getState());
        String lowerMessage = customerMessage.toLowerCase();
        List<String> actions = new ArrayList<>();

        // Handle confirmations
        if ((lowerMessage.equals("yes") || lowerMessage.equals("confirm") || lowerMessage.contains("confirm"))
                && state == ConversationState.CONFIRMING_ORDER) {
            ConversationStateMachine.CartOperationResult result = stateMachine.confirmOrder(conversation);
            return AIResponse.builder()
                    .message(result.getMessage())
                    .requiresPaymentLink(result.isRequiresPaymentLink())
                    .suggestedState(ConversationState.AWAITING_PAYMENT.getValue())
                    .orderContext(orderContext)
                    .build();
        }

        // Handle cancellation
        if (lowerMessage.contains("cancel") || lowerMessage.contains("forget it") || lowerMessage.equals("no")) {
            if (state.isOrdering()) {
                stateMachine.cancelOrder(conversation);
                return AIResponse.builder()
                        .message("No problem! Your order has been cancelled. Is there anything else I can help you with?")
                        .suggestedState(ConversationState.BROWSING.getValue())
                        .build();
            }
        }

        // Show cart if requested
        if (lowerMessage.contains("cart") || lowerMessage.contains("order") && lowerMessage.contains("what")) {
            if (!orderContext.isEmpty()) {
                return AIResponse.builder()
                        .message(orderContext.getCartSummary())
                        .orderContext(orderContext)
                        .build();
            } else {
                return AIResponse.builder()
                        .message("Your cart is empty. Would you like to see our products?")
                        .build();
            }
        }

        // Try to detect order intent with product and quantity
        Pattern orderPattern = Pattern.compile(
                "(\\d+)\\s*(?:pieces?|pcs?|x)?\\s*(?:of\\s+)?(.+)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher orderMatcher = orderPattern.matcher(customerMessage);
        if (orderMatcher.find() || lowerMessage.contains("want") || lowerMessage.contains("order") || lowerMessage.contains("buy")) {
            int quantity = 1;
            String productQuery = customerMessage;

            if (orderMatcher.find()) {
                quantity = Integer.parseInt(orderMatcher.group(1));
                productQuery = orderMatcher.group(2).trim();
            }

            // Try to find matching product
            List<Product> products = productRepository.findByBusinessIdAndStatusOrderByNameAsc(
                    businessId, "active");

            for (Product p : products) {
                if (productQuery.toLowerCase().contains(p.getName().toLowerCase()) ||
                        p.getName().toLowerCase().contains(productQuery.toLowerCase())) {
                    ConversationStateMachine.CartOperationResult result = stateMachine.addToCart(
                            conversation, p.getId(), quantity
                    );
                    if (result.isSuccess()) {
                        actions.add("ADD_TO_CART:" + p.getName() + "," + quantity);
                        String msg = result.getMessage() + "\n\n" + result.getOrderContext().getCartSummary();
                        msg += "\n\nWould you like anything else, or should we proceed with delivery?";
                        return AIResponse.builder()
                                .message(msg)
                                .executedActions(actions)
                                .suggestedState(ConversationState.ADDING_TO_CART.getValue())
                                .orderContext(result.getOrderContext())
                                .build();
                    } else {
                        return AIResponse.builder()
                                .message(result.getMessage())
                                .build();
                    }
                }
            }
        }

        // Standard fallback responses
        if (isGreeting(lowerMessage)) {
            response = config != null && config.getGreetingMessage() != null ?
                    config.getGreetingMessage() :
                    "Hello! Welcome to our store. How can I help you today?";
        } else if (lowerMessage.contains("price") || lowerMessage.contains("cost") || lowerMessage.contains("how much")) {
            List<Product> products = productRepository.findByBusinessIdAndStatusOrderByNameAsc(
                    businessId, "active");
            if (!products.isEmpty()) {
                StringBuilder sb = new StringBuilder("Here are our products:\n\n");
                for (Product p : products.subList(0, Math.min(5, products.size()))) {
                    sb.append("- ").append(p.getName()).append(": NGN ")
                            .append(p.getPrice().setScale(0, RoundingMode.HALF_UP)).append("\n");
                }
                sb.append("\nWhich one interests you?");
                response = sb.toString();
            } else {
                response = "I'd be happy to help with pricing! What product are you interested in?";
            }
        } else if (lowerMessage.contains("delivery") || lowerMessage.contains("address")) {
            if (!orderContext.isEmpty()) {
                response = "Please provide your delivery address and I'll calculate the delivery fee.";
            } else {
                String dispatchTime = config != null && config.getDispatchTime() != null ?
                        config.getDispatchTime() : "24-48 hours";
                response = "We dispatch orders within " + dispatchTime + ". Would you like to place an order?";
            }
        } else if (lowerMessage.contains("pay") || lowerMessage.contains("transfer")) {
            response = "Once you confirm your order, I'll send you a secure payment link. You can pay with card or bank transfer.";
        } else if (lowerMessage.contains("help") || lowerMessage.contains("human") || lowerMessage.contains("speak")) {
            return AIResponse.builder()
                    .message("Let me connect you with our team. Someone will respond shortly!")
                    .shouldHandoff(true)
                    .handoffReason("Customer requested human assistance")
                    .build();
        } else if (lowerMessage.contains("thank")) {
            response = "You're welcome! Is there anything else I can help you with?";
        } else if (lowerMessage.contains("product") || lowerMessage.contains("menu") || lowerMessage.contains("list")) {
            List<Product> products = productRepository.findByBusinessIdAndStatusOrderByNameAsc(
                    businessId, "active");
            if (!products.isEmpty()) {
                StringBuilder sb = new StringBuilder("Here's what we have:\n\n");
                for (Product p : products) {
                    sb.append("- ").append(p.getName()).append(": NGN ")
                            .append(p.getPrice().setScale(0, RoundingMode.HALF_UP)).append("\n");
                }
                sb.append("\nWhich one would you like?");
                response = sb.toString();
            } else {
                response = "Our product catalog is being updated. Please check back soon!";
            }
        } else {
            response = "I'm here to help! You can:\n- View our products\n- Place an order\n- Ask about delivery\n\nWhat would you like to do?";
        }

        return AIResponse.builder()
                .message(response)
                .shouldHandoff(false)
                .orderContext(orderContext)
                .build();
    }

    private boolean isGreeting(String message) {
        String[] greetings = {"hi", "hello", "hey", "good morning", "good afternoon", "good evening",
                "howdy", "greetings", "what's up", "wassup", "sup"};
        for (String g : greetings) {
            if (message.contains(g)) {
                return true;
            }
        }
        return false;
    }

    @Data
    @Builder
    public static class AIResponse {
        private String message;
        private boolean shouldHandoff;
        private String handoffReason;
        private String suggestedState;
        private List<String> detectedIntents;
        private List<String> executedActions;
        private Map<String, Object> extractedEntities;
        private OrderContext orderContext;
        private boolean requiresPaymentLink;
    }
}
