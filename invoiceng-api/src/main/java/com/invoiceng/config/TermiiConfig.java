package com.invoiceng.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "termii")
@Data
public class TermiiConfig {

    private String apiKey;
    private String senderId;
    private String baseUrl = "https://api.ng.termii.com";
}
