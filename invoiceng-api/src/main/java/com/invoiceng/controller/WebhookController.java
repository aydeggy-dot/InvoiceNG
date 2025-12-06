package com.invoiceng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceng.service.PaymentService;
import com.invoiceng.service.PaystackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhooks", description = "Webhook endpoints for external services")
public class WebhookController {

    private final PaystackService paystackService;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @PostMapping("/paystack")
    @Operation(summary = "Paystack webhook", description = "Handle Paystack payment webhooks")
    public ResponseEntity<Void> handlePaystackWebhook(
            @RequestBody String payload,
            @RequestHeader("x-paystack-signature") String signature
    ) {
        log.info("Received Paystack webhook");

        // Verify signature
        if (!paystackService.verifyWebhookSignature(payload, signature)) {
            log.warn("Invalid Paystack webhook signature");
            return ResponseEntity.status(401).build();
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = objectMapper.readValue(payload, Map.class);
            String eventType = (String) event.get("event");

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) event.get("data");

            log.info("Processing Paystack event: {}", eventType);

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
            log.error("Error processing Paystack webhook", e);
            // Always return 200 to prevent retries
            return ResponseEntity.ok().build();
        }
    }
}
