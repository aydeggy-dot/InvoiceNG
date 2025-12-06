package com.invoiceng.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "phone"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "payment_score")
    @Builder.Default
    private Integer paymentScore = 100;

    @Column(name = "total_invoices")
    @Builder.Default
    private Integer totalInvoices = 0;

    @Column(name = "total_paid", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(name = "total_outstanding", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalOutstanding = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void incrementInvoiceCount() {
        this.totalInvoices++;
    }

    public void addPayment(BigDecimal amount) {
        this.totalPaid = this.totalPaid.add(amount);
        this.totalOutstanding = this.totalOutstanding.subtract(amount);
        if (this.totalOutstanding.compareTo(BigDecimal.ZERO) < 0) {
            this.totalOutstanding = BigDecimal.ZERO;
        }
    }

    public void addOutstanding(BigDecimal amount) {
        this.totalOutstanding = this.totalOutstanding.add(amount);
    }
}
