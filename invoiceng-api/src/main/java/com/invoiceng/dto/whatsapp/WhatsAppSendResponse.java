package com.invoiceng.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhatsAppSendResponse {
    @JsonProperty("messaging_product")
    private String messagingProduct;
    private List<Contact> contacts;
    private List<Message> messages;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Contact {
        private String input;
        @JsonProperty("wa_id")
        private String waId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String id;
    }

    public String getFirstMessageId() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(0).getId();
        }
        return null;
    }
}
