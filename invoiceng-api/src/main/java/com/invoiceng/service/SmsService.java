package com.invoiceng.service;

import com.invoiceng.config.TermiiConfig;
import com.invoiceng.exception.SmsException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final TermiiConfig termiiConfig;
    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder
                .baseUrl(termiiConfig.getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Send OTP via Termii
     * Returns the pin_id for verification
     */
    public String sendOtp(String phone) {
        log.info("Sending OTP to phone: {}", maskPhone(phone));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("api_key", termiiConfig.getApiKey());
        requestBody.put("message_type", "NUMERIC");
        requestBody.put("to", phone);
        requestBody.put("from", termiiConfig.getSenderId());
        requestBody.put("channel", "dnd");
        requestBody.put("pin_attempts", 3);
        requestBody.put("pin_time_to_live", 10);
        requestBody.put("pin_length", 6);
        requestBody.put("pin_placeholder", "< 1234 >");
        requestBody.put("message_text", "Your InvoiceNG verification code is < 1234 >. Valid for 10 minutes. Do not share this code.");
        requestBody.put("pin_type", "NUMERIC");

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                    .uri("/api/sms/otp/send")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && "200".equals(String.valueOf(response.get("status")))) {
                String pinId = (String) response.get("pinId");
                log.info("OTP sent successfully, pinId: {}", pinId);
                return pinId;
            }

            log.error("Failed to send OTP: {}", response);
            throw new SmsException("Failed to send OTP");

        } catch (WebClientResponseException e) {
            log.error("Termii API error: {}", e.getResponseBodyAsString());
            throw new SmsException("SMS service error", e);
        }
    }

    /**
     * Verify OTP via Termii
     */
    public boolean verifyOtp(String pinId, String otp) {
        log.info("Verifying OTP for pinId: {}", pinId);

        Map<String, Object> requestBody = Map.of(
                "api_key", termiiConfig.getApiKey(),
                "pin_id", pinId,
                "pin", otp
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                    .uri("/api/sms/otp/verify")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                String verified = String.valueOf(response.get("verified"));
                boolean isVerified = "Verified".equalsIgnoreCase(verified) || "true".equalsIgnoreCase(verified);
                log.info("OTP verification result: {}", isVerified);
                return isVerified;
            }

            return false;

        } catch (WebClientResponseException e) {
            log.error("Termii verify error: {}", e.getResponseBodyAsString());
            return false;
        }
    }

    /**
     * Send regular SMS (for reminders)
     */
    public void sendSms(String phone, String message) {
        log.info("Sending SMS to: {}", maskPhone(phone));

        Map<String, Object> requestBody = Map.of(
                "api_key", termiiConfig.getApiKey(),
                "to", phone,
                "from", termiiConfig.getSenderId(),
                "sms", message,
                "type", "plain",
                "channel", "generic"
        );

        try {
            webClient.post()
                    .uri("/api/sms/send")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("SMS sent successfully");

        } catch (WebClientResponseException e) {
            log.error("Failed to send SMS: {}", e.getResponseBodyAsString());
            throw new SmsException("Failed to send SMS", e);
        }
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 6) {
            return "****";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
