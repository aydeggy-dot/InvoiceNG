package com.invoiceng.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWhatsAppOrderRequest {

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String deliveryAddress;

    @Size(max = 100, message = "Delivery area must be less than 100 characters")
    private String deliveryArea;

    private BigDecimal deliveryFee;

    private String deliveryNotes;

    private String paymentStatus;

    private String paymentMethod;

    private String paymentReference;

    private String fulfillmentStatus;

    private String trackingNumber;

    private String internalNotes;
}
