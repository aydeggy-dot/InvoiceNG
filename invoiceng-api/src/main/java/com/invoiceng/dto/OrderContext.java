package com.invoiceng.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the order context stored in conversation.context JSONB field.
 * Contains all the information needed to create an invoice from a WhatsApp conversation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderContext {

    /**
     * Customer's delivery address
     */
    private String deliveryAddress;

    /**
     * Specific delivery area/zone for fee calculation
     */
    private String deliveryArea;

    /**
     * Calculated delivery fee
     */
    private BigDecimal deliveryFee;

    /**
     * Customer's preferred delivery date/time
     */
    private String deliveryNotes;

    /**
     * Contact phone for delivery (may be different from WhatsApp number)
     */
    private String deliveryPhone;

    /**
     * Any special instructions for the order
     */
    private String specialInstructions;

    /**
     * Cart items
     */
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    /**
     * Subtotal (sum of all line items)
     */
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    /**
     * Total discount applied
     */
    @Builder.Default
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    /**
     * Grand total including delivery
     */
    @Builder.Default
    private BigDecimal grandTotal = BigDecimal.ZERO;

    /**
     * Whether customer has confirmed the order
     */
    @Builder.Default
    private boolean confirmed = false;

    /**
     * Payment link generated for this order
     */
    private String paymentLink;

    /**
     * Invoice ID once created
     */
    private String invoiceId;

    /**
     * Add item to cart
     */
    public void addItem(CartItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        // Check if product already in cart
        for (CartItem existing : this.items) {
            if (existing.getProductId() != null && existing.getProductId().equals(item.getProductId())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                existing.calculateTotals();
                recalculateTotals();
                return;
            }
        }
        item.calculateTotals();
        this.items.add(item);
        recalculateTotals();
    }

    /**
     * Update item quantity
     */
    public void updateItemQuantity(int index, int newQuantity) {
        if (this.items != null && index >= 0 && index < this.items.size()) {
            if (newQuantity <= 0) {
                this.items.remove(index);
            } else {
                this.items.get(index).setQuantity(newQuantity);
                this.items.get(index).calculateTotals();
            }
            recalculateTotals();
        }
    }

    /**
     * Remove item from cart
     */
    public void removeItem(int index) {
        if (this.items != null && index >= 0 && index < this.items.size()) {
            this.items.remove(index);
            recalculateTotals();
        }
    }

    /**
     * Clear cart
     */
    public void clearCart() {
        if (this.items != null) {
            this.items.clear();
        }
        this.subtotal = BigDecimal.ZERO;
        this.totalDiscount = BigDecimal.ZERO;
        this.grandTotal = BigDecimal.ZERO;
    }

    /**
     * Recalculate all totals
     */
    public void recalculateTotals() {
        this.subtotal = BigDecimal.ZERO;
        this.totalDiscount = BigDecimal.ZERO;

        if (this.items != null) {
            for (CartItem item : this.items) {
                this.subtotal = this.subtotal.add(item.getLineTotal());
                // Calculate discount saved
                if (item.getDiscountPercent() != null && item.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal originalTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    this.totalDiscount = this.totalDiscount.add(originalTotal.subtract(item.getLineTotal()));
                }
            }
        }

        // Add delivery fee to grand total
        BigDecimal delivery = this.deliveryFee != null ? this.deliveryFee : BigDecimal.ZERO;
        this.grandTotal = this.subtotal.add(delivery);
    }

    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        return this.items == null || this.items.isEmpty();
    }

    /**
     * Get cart summary for display
     */
    public String getCartSummary() {
        if (isEmpty()) {
            return "Your cart is empty.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("*Your Order:*\n\n");

        int index = 1;
        for (CartItem item : this.items) {
            sb.append(index++).append(". ")
                    .append(item.getProductName())
                    .append(" x").append(item.getQuantity())
                    .append(" - NGN ").append(item.getLineTotal().setScale(0, java.math.RoundingMode.HALF_UP).toPlainString())
                    .append("\n");
        }

        sb.append("\n*Subtotal:* NGN ").append(this.subtotal.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString());

        if (this.totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("\n*Discount:* -NGN ").append(this.totalDiscount.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString());
        }

        if (this.deliveryFee != null && this.deliveryFee.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("\n*Delivery:* NGN ").append(this.deliveryFee.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString());
        }

        sb.append("\n\n*Total:* NGN ").append(this.grandTotal.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString());

        return sb.toString();
    }

    /**
     * Check if all required info is collected
     */
    public boolean isReadyForConfirmation() {
        return !isEmpty() &&
                this.deliveryAddress != null && !this.deliveryAddress.isBlank();
    }

    /**
     * Get total item count
     */
    public int getTotalItemCount() {
        if (this.items == null) return 0;
        return this.items.stream().mapToInt(CartItem::getQuantity).sum();
    }
}
