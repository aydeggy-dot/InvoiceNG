package com.invoiceng.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents an item in the conversation cart.
 * Stored in the conversation's cart JSONB field.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    /**
     * Product ID from the products table
     */
    private UUID productId;

    /**
     * Product name (denormalized for display)
     */
    private String productName;

    /**
     * Quantity ordered
     */
    private int quantity;

    /**
     * Unit price at time of adding to cart
     */
    private BigDecimal unitPrice;

    /**
     * Any negotiated discount percentage
     */
    @Builder.Default
    private BigDecimal discountPercent = BigDecimal.ZERO;

    /**
     * Final price per unit after discount
     */
    private BigDecimal finalUnitPrice;

    /**
     * Total for this line item (quantity * finalUnitPrice)
     */
    private BigDecimal lineTotal;

    /**
     * Any notes for this item (e.g., "no onions", "gift wrap")
     */
    private String notes;

    /**
     * Calculate line total based on quantity and price
     */
    public void calculateTotals() {
        if (this.unitPrice == null) {
            this.unitPrice = BigDecimal.ZERO;
        }

        // Apply discount
        if (this.discountPercent != null && this.discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                    this.discountPercent.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP)
            );
            this.finalUnitPrice = this.unitPrice.multiply(discountMultiplier)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        } else {
            this.finalUnitPrice = this.unitPrice;
        }

        // Calculate line total
        this.lineTotal = this.finalUnitPrice.multiply(BigDecimal.valueOf(this.quantity))
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
