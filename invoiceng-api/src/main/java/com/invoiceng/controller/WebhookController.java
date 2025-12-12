package com.invoiceng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceng.dto.paystack.PaystackWebhookEvent;
import com.invoiceng.service.PaymentService;
import com.invoiceng.service.PaymentWebhookService;
import com.invoiceng.service.PaystackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhooks", description = "Webhook endpoints for external services")
public class WebhookController {

    private final PaystackService paystackService;
    private final PaymentService paymentService;
    private final PaymentWebhookService paymentWebhookService;
    private final ObjectMapper objectMapper;

    @PostMapping("/paystack")
    @Operation(summary = "Paystack webhook", description = "Handle Paystack payment webhooks")
    public ResponseEntity<Void> handlePaystackWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "x-paystack-signature", required = false) String signature
    ) {
        log.info("Received Paystack webhook");

        // Verify signature if present
        if (signature != null && !signature.isBlank()) {
            if (!paystackService.verifyWebhookSignature(payload, signature)) {
                log.warn("Invalid Paystack webhook signature");
                return ResponseEntity.status(401).build();
            }
        }

        // Parse the event
        PaystackWebhookEvent event;
        try {
            event = objectMapper.readValue(payload, PaystackWebhookEvent.class);
        } catch (Exception e) {
            log.error("Failed to parse Paystack webhook payload: {}", e.getMessage());
            // Try the legacy handler
            return handlePaystackWebhookLegacy(payload);
        }

        log.info("Processing Paystack event: {} for reference: {}",
                event.getEvent(),
                event.getData() != null ? event.getData().getReference() : "unknown");

        // Process asynchronously to avoid timeout
        CompletableFuture.runAsync(() -> processPaystackEvent(event))
                .exceptionally(ex -> {
                    log.error("Error processing Paystack webhook", ex);
                    return null;
                });

        // Always return 200 quickly to acknowledge receipt
        return ResponseEntity.ok().build();
    }

    private void processPaystackEvent(PaystackWebhookEvent event) {
        if (event == null || event.getData() == null) {
            log.warn("Empty Paystack webhook event");
            return;
        }

        String eventType = event.getEvent();

        switch (eventType) {
            case "charge.success" -> {
                if (event.isPaymentSuccessful()) {
                    // Handle WhatsApp order payments
                    paymentWebhookService.handlePaymentSuccess(event);

                    // Also call legacy handler for invoice payments
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> data = objectMapper.convertValue(event.getData(), Map.class);
                        paymentService.handleSuccessfulPayment(data);
                    } catch (Exception e) {
                        log.debug("Legacy payment handler skipped: {}", e.getMessage());
                    }
                }
            }
            case "charge.failed" -> {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = objectMapper.convertValue(event.getData(), Map.class);
                    paymentService.handleFailedPayment(data);
                } catch (Exception e) {
                    log.error("Error handling failed payment: {}", e.getMessage());
                }
            }
            default -> log.debug("Unhandled Paystack event type: {}", eventType);
        }
    }

    /**
     * Legacy handler for backward compatibility
     */
    private ResponseEntity<Void> handlePaystackWebhookLegacy(String payload) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = objectMapper.readValue(payload, Map.class);
            String eventType = (String) event.get("event");

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) event.get("data");

            log.info("Processing Paystack event (legacy): {}", eventType);

            switch (eventType) {
                case "charge.success":
                    paymentService.handleSuccessfulPayment(data);
                    break;
                case "charge.failed":
                    paymentService.handleFailedPayment(data);
                    break;
                default:
                    log.info("Unhandled Paystack event: {}", eventType);
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Error processing Paystack webhook (legacy)", e);
            // Always return 200 to prevent retries
            return ResponseEntity.ok().build();
        }
    }
}
