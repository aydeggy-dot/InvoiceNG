package com.invoiceng.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "agent_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false, unique = true)
    private User business;

    @Column(name = "agent_name", length = 100)
    private String agentName;

    @Column(name = "greeting_message", columnDefinition = "TEXT")
    private String greetingMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "personality", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> personality = Map.of(
            "friendliness", 0.8,
            "formality", 0.5,
            "emoji_usage", "moderate",
            "language", "english_nigerian"
    );

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sales_settings", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> salesSettings = Map.of(
            "max_discount_percent", 10,
            "min_price_percent", 85,
            "negotiation_enabled", true,
            "upsell_enabled", true,
            "bulk_discount_enabled", true,
            "bulk_discount_threshold", 3,
            "bulk_discount_percent", 5
    );

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "business_hours", columnDefinition = "jsonb")
    private Map<String, Object> businessHours;

    @Column(name = "after_hours_behavior", length = 20)
    @Builder.Default
    private String afterHoursBehavior = "ai_only";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "handoff_triggers", columnDefinition = "jsonb")
    private List<String> handoffTriggers;

    @Column(name = "handoff_notification_method", length = 20)
    @Builder.Default
    private String handoffNotificationMethod = "push";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "templates", columnDefinition = "jsonb")
    private Map<String, String> templates;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "delivery_areas", columnDefinition = "jsonb")
    private List<Map<String, Object>> deliveryAreas;

    @Column(name = "default_delivery_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal defaultDeliveryFee = BigDecimal.ZERO;

    @Column(name = "dispatch_time", length = 100)
    @Builder.Default
    private String dispatchTime = "24-48 hours";

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public int getMaxDiscountPercent() {
        if (salesSettings != null && salesSettings.containsKey("max_discount_percent")) {
            return ((Number) salesSettings.get("max_discount_percent")).intValue();
        }
        return 10;
    }

    public int getMinPricePercent() {
        if (salesSettings != null && salesSettings.containsKey("min_price_percent")) {
            return ((Number) salesSettings.get("min_price_percent")).intValue();
        }
        return 85;
    }

    public boolean isNegotiationEnabled() {
        if (salesSettings != null && salesSettings.containsKey("negotiation_enabled")) {
            return (Boolean) salesSettings.get("negotiation_enabled");
        }
        return true;
    }

    public String getEmojiUsage() {
        if (personality != null && personality.containsKey("emoji_usage")) {
            return (String) personality.get("emoji_usage");
        }
        return "moderate";
    }

    public String getLanguage() {
        if (personality != null && personality.containsKey("language")) {
            return (String) personality.get("language");
        }
        return "english_nigerian";
    }
}
