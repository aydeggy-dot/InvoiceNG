package com.invoiceng.service.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceng.config.ClaudeConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaudeService {

    private final WebClient claudeWebClient;
    private final ClaudeConfig claudeConfig;
    private final ObjectMapper objectMapper;

    public String chat(String systemPrompt, List<Message> messages) {
        try {
            ClaudeRequest request = new ClaudeRequest();
            request.setModel(claudeConfig.getModel());
            request.setMaxTokens(claudeConfig.getMaxTokens());
            request.setSystem(systemPrompt);
            request.setMessages(messages.stream()
                    .map(m -> Map.of("role", m.getRole(), "content", m.getContent()))
                    .toList());

            log.debug("Calling Claude API with model: {}, maxTokens: {}", claudeConfig.getModel(), claudeConfig.getMaxTokens());

            ClaudeResponse response = claudeWebClient.post()
                    .uri("/v1/messages")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(body -> {
                                        log.error("Claude API error response: {}", body);
                                        return new RuntimeException("Claude API error: " + body);
                                    }))
                    .bodyToMono(ClaudeResponse.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (response != null && response.getContent() != null && !response.getContent().isEmpty()) {
                return response.getContent().get(0).getText();
            }

            log.warn("Empty response from Claude API");
            return null;
        } catch (Exception e) {
            log.error("Error calling Claude API: {}", e.getMessage(), e);
            return null;
        }
    }

    public Mono<String> chatAsync(String systemPrompt, List<Message> messages) {
        ClaudeRequest request = new ClaudeRequest();
        request.setModel(claudeConfig.getModel());
        request.setMaxTokens(claudeConfig.getMaxTokens());
        request.setSystem(systemPrompt);
        request.setMessages(messages.stream()
                .map(m -> Map.of("role", m.getRole(), "content", m.getContent()))
                .toList());

        return claudeWebClient.post()
                .uri("/v1/messages")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ClaudeResponse.class)
                .timeout(Duration.ofSeconds(30))
                .map(response -> {
                    if (response.getContent() != null && !response.getContent().isEmpty()) {
                        return response.getContent().get(0).getText();
                    }
                    return "";
                })
                .onErrorResume(e -> {
                    log.error("Error calling Claude API async: {}", e.getMessage());
                    return Mono.just("");
                });
    }

    public boolean isConfigured() {
        return claudeConfig.getApiKey() != null && !claudeConfig.getApiKey().isBlank();
    }

    @Data
    public static class Message {
        private String role;
        private String content;

        public static Message user(String content) {
            Message m = new Message();
            m.setRole("user");
            m.setContent(content);
            return m;
        }

        public static Message assistant(String content) {
            Message m = new Message();
            m.setRole("assistant");
            m.setContent(content);
            return m;
        }
    }

    @Data
    private static class ClaudeRequest {
        private String model;
        @JsonProperty("max_tokens")
        private int maxTokens;
        private String system;
        private List<Map<String, String>> messages;
    }

    @Data
    private static class ClaudeResponse {
        private String id;
        private String type;
        private String role;
        private List<ContentBlock> content;
        private String model;
        @JsonProperty("stop_reason")
        private String stopReason;
        private Usage usage;

        @Data
        public static class ContentBlock {
            private String type;
            private String text;
        }

        @Data
        public static class Usage {
            @JsonProperty("input_tokens")
            private int inputTokens;
            @JsonProperty("output_tokens")
            private int outputTokens;
        }
    }
}
