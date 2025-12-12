package com.invoiceng.dto.response;

import com.invoiceng.entity.WhatsAppOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppOrderResponse {

    private UUID id;
    private String orderNumber;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String deliveryAddress;
    private String deliveryArea;
    private BigDecimal deliveryFee;
    private String deliveryNotes;
    private List<Map<String, Object>> items;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private String discountReason;
    private BigDecimal total;
    private String paymentStatus;
    private String paymentMethod;
    private String paymentReference;
    private String paymentLink;
    private LocalDateTime paidAt;
    private String fulfillmentStatus;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private String trackingNumber;
    private String internalNotes;
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WhatsAppOrderResponse fromEntity(WhatsAppOrder order) {
        return WhatsAppOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .customerEmail(order.getCustomerEmail())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryArea(order.getDeliveryArea())
                .deliveryFee(order.getDeliveryFee())
                .deliveryNotes(order.getDeliveryNotes())
                .items(order.getItems())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .discountReason(order.getDiscountReason())
                .total(order.getTotal())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentReference(order.getPaymentReference())
                .paymentLink(order.getPaymentLink())
                .paidAt(order.getPaidAt())
                .fulfillmentStatus(order.getFulfillmentStatus())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .trackingNumber(order.getTrackingNumber())
                .internalNotes(order.getInternalNotes())
                .source(order.getSource())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public static WhatsAppOrderResponse fromEntityBasic(WhatsAppOrder order) {
        return WhatsAppOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .total(order.getTotal())
                .paymentStatus(order.getPaymentStatus())
                .fulfillmentStatus(order.getFulfillmentStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
