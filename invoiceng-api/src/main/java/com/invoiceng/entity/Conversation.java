package com.invoiceng.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "conversations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"business_id", "customer_phone"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private User business;

    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_whatsapp_id", length = 100)
    private String customerWhatsappId;

    @Column(name = "state", length = 50)
    @Builder.Default
    private String state = "greeting";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "context", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> context = Map.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cart", columnDefinition = "jsonb")
    private String cart;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "message_count")
    @Builder.Default
    private Integer messageCount = 0;

    @Column(name = "is_handed_off")
    @Builder.Default
    private Boolean isHandedOff = false;

    @Column(name = "handed_off_at")
    private LocalDateTime handedOffAt;

    @Column(name = "handed_off_reason")
    private String handedOffReason;

    @Column(name = "outcome", length = 20)
    private String outcome;

    @Column(name = "order_id")
    private UUID orderId;

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

    public void incrementMessageCount() {
        this.messageCount = (this.messageCount == null ? 0 : this.messageCount) + 1;
        this.lastMessageAt = LocalDateTime.now();
    }

    public void handOff(String reason) {
        this.isHandedOff = true;
        this.handedOffAt = LocalDateTime.now();
        this.handedOffReason = reason;
    }

    public void markAsConverted(UUID orderId) {
        this.outcome = "converted";
        this.orderId = orderId;
        this.isActive = false;
    }

    public void markAsAbandoned() {
        this.outcome = "abandoned";
        this.isActive = false;
    }
}
