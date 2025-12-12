package com.invoiceng.entity;

/**
 * Represents the states in a WhatsApp sales conversation flow.
 *
 * State Flow:
 * GREETING -> BROWSING -> PRODUCT_INQUIRY -> ADDING_TO_CART -> COLLECTING_ADDRESS ->
 * CONFIRMING_ORDER -> AWAITING_PAYMENT -> COMPLETED
 *
 * Any state can transition to HANDED_OFF if human intervention is needed.
 */
public enum ConversationState {

    /**
     * Initial state - customer just started the conversation
     */
    GREETING("greeting"),

    /**
     * Customer is browsing products, asking general questions
     */
    BROWSING("browsing"),

    /**
     * Customer is asking about specific products (price, availability, details)
     */
    PRODUCT_INQUIRY("product_inquiry"),

    /**
     * Customer has expressed intent to buy, adding items to cart
     */
    ADDING_TO_CART("adding_to_cart"),

    /**
     * Cart has items, collecting delivery address
     */
    COLLECTING_ADDRESS("collecting_address"),

    /**
     * All order details collected, awaiting customer confirmation
     */
    CONFIRMING_ORDER("confirming_order"),

    /**
     * Order confirmed, payment link sent, awaiting payment
     */
    AWAITING_PAYMENT("awaiting_payment"),

    /**
     * Payment received, order complete
     */
    COMPLETED("completed"),

    /**
     * Conversation handed off to human agent
     */
    HANDED_OFF("handed_off"),

    /**
     * Customer abandoned the conversation (no activity for X hours)
     */
    ABANDONED("abandoned");

    private final String value;

    ConversationState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ConversationState fromValue(String value) {
        if (value == null) {
            return GREETING;
        }
        for (ConversationState state : values()) {
            if (state.value.equalsIgnoreCase(value)) {
                return state;
            }
        }
        return GREETING;
    }

    /**
     * Check if this state allows adding items to cart
     */
    public boolean canAddToCart() {
        return this == GREETING || this == BROWSING || this == PRODUCT_INQUIRY || this == ADDING_TO_CART;
    }

    /**
     * Check if this state is an active sales state (not completed/abandoned/handed off)
     */
    public boolean isActive() {
        return this != COMPLETED && this != ABANDONED && this != HANDED_OFF;
    }

    /**
     * Check if order is in progress (has cart items and collecting details)
     */
    public boolean isOrdering() {
        return this == ADDING_TO_CART || this == COLLECTING_ADDRESS || this == CONFIRMING_ORDER;
    }
}
