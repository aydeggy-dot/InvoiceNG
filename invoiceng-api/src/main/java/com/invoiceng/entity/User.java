package com.invoiceng.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(name = "business_name", length = 255)
    private String businessName;

    @Column(name = "business_address", columnDefinition = "TEXT")
    private String businessAddress;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_code", length = 10)
    private String bankCode;

    @Column(name = "account_number", length = 20)
    private String accountNumber;

    @Column(name = "account_name", length = 255)
    private String accountName;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "subscription_tier", length = 20)
    @Builder.Default
    private String subscriptionTier = "free";

    @Column(name = "invoice_count_this_month")
    @Builder.Default
    private Integer invoiceCountThisMonth = 0;

    @Column(name = "invoice_count_reset_at")
    private LocalDateTime invoiceCountResetAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
