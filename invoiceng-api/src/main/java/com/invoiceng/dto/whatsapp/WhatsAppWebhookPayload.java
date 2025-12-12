package com.invoiceng.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhatsAppWebhookPayload {
    private String object;
    private List<Entry> entry;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry {
        private String id;
        private List<Change> changes;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Change {
        private String field;
        private Value value;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Value {
        @JsonProperty("messaging_product")
        private String messagingProduct;
        private Metadata metadata;
        private List<Contact> contacts;
        private List<Message> messages;
        private List<Status> statuses;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        @JsonProperty("display_phone_number")
        private String displayPhoneNumber;
        @JsonProperty("phone_number_id")
        private String phoneNumberId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Contact {
        private Profile profile;
        @JsonProperty("wa_id")
        private String waId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String id;
        private String from;
        private String timestamp;
        private String type;
        private TextMessage text;
        private ImageMessage image;
        private AudioMessage audio;
        private DocumentMessage document;
        private InteractiveMessage interactive;
        private ButtonMessage button;
        private Context context;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TextMessage {
        private String body;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageMessage {
        private String id;
        private String caption;
        @JsonProperty("mime_type")
        private String mimeType;
        private String sha256;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AudioMessage {
        private String id;
        @JsonProperty("mime_type")
        private String mimeType;
        private String sha256;
        private Boolean voice;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DocumentMessage {
        private String id;
        private String caption;
        private String filename;
        @JsonProperty("mime_type")
        private String mimeType;
        private String sha256;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InteractiveMessage {
        private String type;
        @JsonProperty("button_reply")
        private ButtonReply buttonReply;
        @JsonProperty("list_reply")
        private ListReply listReply;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ButtonReply {
        private String id;
        private String title;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListReply {
        private String id;
        private String title;
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ButtonMessage {
        private String payload;
        private String text;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Context {
        private String from;
        private String id;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status {
        private String id;
        @JsonProperty("recipient_id")
        private String recipientId;
        private String status;
        private String timestamp;
        private Conversation conversation;
        private Pricing pricing;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Conversation {
        private String id;
        private Origin origin;
        @JsonProperty("expiration_timestamp")
        private String expirationTimestamp;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Origin {
        private String type;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pricing {
        private String category;
        @JsonProperty("pricing_model")
        private String pricingModel;
        private Boolean billable;
    }
}
