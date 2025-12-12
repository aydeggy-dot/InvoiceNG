package com.invoiceng.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "whatsapp")
@Data
public class WhatsAppConfig {
    private String apiVersion = "v18.0";
    private String accessToken;
    private String verifyToken;
    private String phoneNumberId;
    private String baseUrl = "https://graph.facebook.com";

    public String getMessagesUrl(String phoneNumberId) {
        return String.format("%s/%s/%s/messages", baseUrl, apiVersion, phoneNumberId);
    }

    public String getMediaUrl(String mediaId) {
        return String.format("%s/%s/%s", baseUrl, apiVersion, mediaId);
    }
}
