package com.invoiceng.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private User business;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(length = 100)
    private String category;

    @Column(length = 100)
    private String subcategory;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "compare_at_price", precision = 12, scale = 2)
    private BigDecimal compareAtPrice;

    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "min_price", precision = 12, scale = 2)
    private BigDecimal minPrice;

    @Column(name = "has_variants")
    @Builder.Default
    private Boolean hasVariants = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variant_options", columnDefinition = "jsonb")
    private Map<String, Object> variantOptions;

    @Column(name = "track_inventory")
    @Builder.Default
    private Boolean trackInventory = false;

    @Column
    @Builder.Default
    private Integer quantity = 0;

    @Column(name = "allow_backorder")
    @Builder.Default
    private Boolean allowBackorder = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_keywords", columnDefinition = "jsonb")
    private List<String> aiKeywords;

    @Column(name = "ai_notes", columnDefinition = "TEXT")
    private String aiNotes;

    @Column(length = 20)
    @Builder.Default
    private String status = "active";

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isActive() {
        return "active".equals(status);
    }

    public boolean isInStock() {
        if (!Boolean.TRUE.equals(trackInventory)) {
            return true;
        }
        return quantity != null && quantity > 0;
    }

    public BigDecimal getEffectiveMinPrice() {
        if (minPrice != null) {
            return minPrice;
        }
        return price.multiply(BigDecimal.valueOf(0.85));
    }
}
