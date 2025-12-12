package com.invoiceng.controller;

import com.invoiceng.config.WhatsAppConfig;
import com.invoiceng.dto.whatsapp.WhatsAppWebhookPayload;
import com.invoiceng.service.WebhookProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks/whatsapp")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "WhatsApp Webhook", description = "WhatsApp Cloud API webhook endpoints")
public class WhatsAppWebhookController {

    private final WhatsAppConfig whatsAppConfig;
    private final WebhookProcessingService webhookProcessingService;

    /**
     * Webhook verification (GET) - Meta sends this to verify webhook
     */
    @GetMapping
    @Operation(summary = "Verify webhook", description = "Endpoint for Meta to verify webhook subscription")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge
    ) {
        log.info("Webhook verification request - mode: {}, token: {}", mode, token);

        if ("subscribe".equals(mode) && whatsAppConfig.getVerifyToken().equals(token)) {
            log.info("Webhook verified successfully");
            return ResponseEntity.ok(challenge);
        }

        log.warn("Webhook verification failed - invalid token");
        return ResponseEntity.status(403).body("Verification failed");
    }

    /**
     * Webhook events (POST) - Receives all WhatsApp events
     */
    @PostMapping
    @Operation(summary = "Handle webhook", description = "Endpoint for receiving WhatsApp webhook events")
    public ResponseEntity<Void> handleWebhook(@RequestBody WhatsAppWebhookPayload payload) {
        log.debug("Received WhatsApp webhook: {}", payload);

        // Always return 200 quickly to acknowledge receipt
        // Process asynchronously using Spring's @Async with proper transaction management
        webhookProcessingService.processWebhookAsync(payload, whatsAppConfig.getPhoneNumberId());

        return ResponseEntity.ok().build();
    }

}
