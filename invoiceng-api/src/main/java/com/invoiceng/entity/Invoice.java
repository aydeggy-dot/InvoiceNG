package com.invoiceng.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "invoice_number", unique = true, nullable = false, length = 50)
    private String invoiceNumber;

    @Type(JsonType.class)
    @Column(name = "items", columnDefinition = "jsonb")
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(name = "issue_date")
    @Builder.Default
    private LocalDate issueDate = LocalDate.now();

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Column(name = "payment_ref", unique = true, length = 100)
    private String paymentRef;

    @Column(name = "payment_link", columnDefinition = "TEXT")
    private String paymentLink;

    @Column(name = "paystack_access_code", length = 100)
    private String paystackAccessCode;

    @Column(name = "pdf_url", columnDefinition = "TEXT")
    private String pdfUrl;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void calculateTotals() {
        this.subtotal = BigDecimal.ZERO;
        if (items != null) {
            for (InvoiceItem item : items) {
                item.calculateTotal();
                this.subtotal = this.subtotal.add(item.getTotal());
            }
        }
        this.total = this.subtotal
                .add(this.tax != null ? this.tax : BigDecimal.ZERO)
                .subtract(this.discount != null ? this.discount : BigDecimal.ZERO);
    }

    public boolean isDraft() {
        return status == InvoiceStatus.DRAFT;
    }

    public boolean isPaid() {
        return status == InvoiceStatus.PAID;
    }

    public boolean isCancelled() {
        return status == InvoiceStatus.CANCELLED;
    }

    public boolean isEditable() {
        return status == InvoiceStatus.DRAFT;
    }

    public void markAsSent() {
        this.status = InvoiceStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markAsViewed() {
        if (this.status == InvoiceStatus.SENT) {
            this.status = InvoiceStatus.VIEWED;
            this.viewedAt = LocalDateTime.now();
        }
    }

    public void markAsPaid() {
        this.status = InvoiceStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void markAsOverdue() {
        if (this.status != InvoiceStatus.PAID && this.status != InvoiceStatus.CANCELLED) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }

    public void cancel() {
        this.status = InvoiceStatus.CANCELLED;
    }
}
