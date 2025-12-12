package com.invoiceng.dto.paystack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackWebhookEvent {

    private String event;
    private PaystackData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaystackData {
        private Long id;
        private String domain;
        private String status;
        private String reference;
        private Integer amount;
        private String message;

        @JsonProperty("gateway_response")
        private String gatewayResponse;

        @JsonProperty("paid_at")
        private String paidAt;

        @JsonProperty("created_at")
        private String createdAt;

        private String channel;
        private String currency;

        @JsonProperty("ip_address")
        private String ipAddress;

        private Map<String, Object> metadata;

        private PaystackCustomer customer;
        private PaystackAuthorization authorization;

        public BigDecimal getAmountInNaira() {
            return amount != null ? BigDecimal.valueOf(amount / 100.0) : BigDecimal.ZERO;
        }

        public String getInvoiceId() {
            if (metadata != null && metadata.containsKey("invoice_id")) {
                return (String) metadata.get("invoice_id");
            }
            return null;
        }

        public String getOrderNumber() {
            // Extract order number from reference (format: WA-ORDER_NUMBER)
            if (reference != null && reference.startsWith("WA-")) {
                return reference.substring(3);
            }
            return reference;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaystackCustomer {
        private Long id;
        private String email;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("customer_code")
        private String customerCode;

        private String phone;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaystackAuthorization {
        @JsonProperty("authorization_code")
        private String authorizationCode;

        private String bin;
        private String last4;

        @JsonProperty("exp_month")
        private String expMonth;

        @JsonProperty("exp_year")
        private String expYear;

        private String channel;

        @JsonProperty("card_type")
        private String cardType;

        private String bank;

        @JsonProperty("country_code")
        private String countryCode;

        private String brand;
        private Boolean reusable;
        private String signature;

        @JsonProperty("account_name")
        private String accountName;
    }

    public boolean isChargeSuccess() {
        return "charge.success".equals(event);
    }

    public boolean isTransferSuccess() {
        return "transfer.success".equals(event);
    }

    public boolean isTransferFailed() {
        return "transfer.failed".equals(event);
    }

    public boolean isPaymentSuccessful() {
        return data != null && "success".equals(data.getStatus());
    }
}
