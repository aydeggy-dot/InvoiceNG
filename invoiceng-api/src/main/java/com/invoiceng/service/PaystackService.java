package com.invoiceng.service;

import com.invoiceng.config.PaystackConfig;
import com.invoiceng.exception.PaymentException;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaystackService {

    private final PaystackConfig paystackConfig;
    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder
                .baseUrl(paystackConfig.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + paystackConfig.getSecretKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Initialize a payment transaction
     */
    public PaystackInitResponse initializeTransaction(
            String reference,
            BigDecimal amount,
            String email,
            String customerName,
            UUID invoiceId
    ) {
        log.info("Initializing Paystack transaction: ref={}, amount={}", reference, amount);

        // Convert to kobo (smallest unit)
        int amountInKobo = amount.multiply(BigDecimal.valueOf(100)).intValue();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("invoice_id", invoiceId.toString());
        metadata.put("customer_name", customerName);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("reference", reference);
        requestBody.put("amount", amountInKobo);
        requestBody.put("email", email);
        requestBody.put("callback_url", paystackConfig.getCallbackUrl());
        requestBody.put("channels", List.of("card", "bank", "ussd", "bank_transfer"));
        requestBody.put("metadata", metadata);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                    .uri("/transaction/initialize")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && Boolean.TRUE.equals(response.get("status"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");

                log.info("Paystack transaction initialized: ref={}", reference);

                return PaystackInitResponse.builder()
                        .authorizationUrl((String) data.get("authorization_url"))
                        .accessCode((String) data.get("access_code"))
                        .reference((String) data.get("reference"))
                        .build();
            }

            String message = response != null ? (String) response.get("message") : "Unknown error";
            throw new PaymentException("Failed to initialize payment: " + message);

        } catch (WebClientResponseException e) {
            log.error("Paystack API error: {}", e.getResponseBodyAsString());
            throw new PaymentException("Payment service error", e);
        }
    }

    /**
     * Verify a transaction
     */
    public PaystackVerifyResponse verifyTransaction(String reference) {
        log.info("Verifying Paystack transaction: ref={}", reference);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.get()
                    .uri("/transaction/verify/{reference}", reference)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && Boolean.TRUE.equals(response.get("status"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");

                String status = (String) data.get("status");
                Integer amountInKobo = (Integer) data.get("amount");
                String channel = (String) data.get("channel");
                String paidAtStr = (String) data.get("paid_at");

                LocalDateTime paidAt = null;
                if (paidAtStr != null) {
                    try {
                        paidAt = LocalDateTime.parse(paidAtStr, DateTimeFormatter.ISO_DATE_TIME);
                    } catch (Exception e) {
                        log.warn("Could not parse paid_at: {}", paidAtStr);
                    }
                }

                return PaystackVerifyResponse.builder()
                        .status(status)
                        .reference(reference)
                        .amount(amountInKobo != null ? BigDecimal.valueOf(amountInKobo / 100.0) : BigDecimal.ZERO)
                        .channel(channel)
                        .paidAt(paidAt)
                        .build();
            }

            return PaystackVerifyResponse.builder()
                    .status("failed")
                    .reference(reference)
                    .build();

        } catch (WebClientResponseException e) {
            log.error("Paystack verify error: {}", e.getResponseBodyAsString());
            throw new PaymentException("Failed to verify payment", e);
        }
    }

    /**
     * Verify webhook signature
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(
                    paystackConfig.getSecretKey().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );
            sha512Hmac.init(keySpec);
            byte[] hash = sha512Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computedSignature = Hex.encodeHexString(hash);
            return computedSignature.equalsIgnoreCase(signature);
        } catch (Exception e) {
            log.error("Error verifying webhook signature", e);
            return false;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class PaystackInitResponse {
        private String authorizationUrl;
        private String accessCode;
        private String reference;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class PaystackVerifyResponse {
        private String status;
        private String reference;
        private BigDecimal amount;
        private String channel;
        private LocalDateTime paidAt;
    }
}
