package com.invoiceng.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWhatsAppOrderRequest {

    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Customer name must be less than 255 characters")
    private String customerName;

    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^(\\+234|234|0)?[789]\\d{9}$", message = "Invalid Nigerian phone number")
    private String customerPhone;

    private String customerEmail;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @Size(max = 100, message = "Delivery area must be less than 100 characters")
    private String deliveryArea;

    private BigDecimal deliveryFee;

    private String deliveryNotes;

    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemRequest> items;

    private BigDecimal discountAmount;

    private String discountReason;

    private String internalNotes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        private UUID productId;
        private UUID variantId;
        private String name;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal total;
    }
}
