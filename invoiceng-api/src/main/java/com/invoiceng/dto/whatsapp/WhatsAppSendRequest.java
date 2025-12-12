package com.invoiceng.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WhatsAppSendRequest {
    @JsonProperty("messaging_product")
    @Builder.Default
    private String messagingProduct = "whatsapp";

    @JsonProperty("recipient_type")
    @Builder.Default
    private String recipientType = "individual";

    private String to;
    private String type;

    // Text message
    private TextContent text;

    // Image message
    private ImageContent image;

    // Document message
    private DocumentContent document;

    // Interactive message (buttons, list)
    private InteractiveContent interactive;

    // Template message
    private TemplateContent template;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TextContent {
        @JsonProperty("preview_url")
        private Boolean previewUrl;
        private String body;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ImageContent {
        private String link;
        private String id;
        private String caption;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DocumentContent {
        private String link;
        private String id;
        private String caption;
        private String filename;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InteractiveContent {
        private String type;
        private Header header;
        private Body body;
        private Footer footer;
        private Action action;

        @Data
        @Builder
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Header {
            private String type;
            private String text;
            private ImageContent image;
            private DocumentContent document;
        }

        @Data
        @Builder
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Body {
            private String text;
        }

        @Data
        @Builder
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Footer {
            private String text;
        }

        @Data
        @Builder
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Action {
            private List<Button> buttons;
            private List<Section> sections;
            private String button;

            @Data
            @Builder
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Button {
                private String type;
                private Reply reply;

                @Data
                @Builder
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Reply {
                    private String id;
                    private String title;
                }
            }

            @Data
            @Builder
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Section {
                private String title;
                private List<Row> rows;

                @Data
                @Builder
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Row {
                    private String id;
                    private String title;
                    private String description;
                }
            }
        }
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TemplateContent {
        private String name;
        private Language language;
        private List<Component> components;

        @Data
        @Builder
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Language {
            private String code;
        }

        @Data
        @Builder
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Component {
            private String type;
            private List<Parameter> parameters;

            @Data
            @Builder
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Parameter {
                private String type;
                private String text;
                private ImageContent image;
                private DocumentContent document;
            }
        }
    }
}
