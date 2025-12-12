package com.invoiceng.dto.response;

import com.invoiceng.entity.Product;
import com.invoiceng.entity.ProductImage;
import com.invoiceng.entity.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private String shortDescription;
    private String category;
    private String subcategory;
    private List<String> tags;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private BigDecimal costPrice;
    private BigDecimal minPrice;
    private Boolean hasVariants;
    private Map<String, Object> variantOptions;
    private Boolean trackInventory;
    private Integer quantity;
    private Boolean allowBackorder;
    private List<String> aiKeywords;
    private String aiNotes;
    private String status;
    private Boolean inStock;
    private List<ProductImageResponse> images;
    private List<ProductVariantResponse> variants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageResponse {
        private UUID id;
        private String url;
        private String altText;
        private Integer position;
        private Boolean isMain;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantResponse {
        private UUID id;
        private String name;
        private String sku;
        private Map<String, Object> options;
        private BigDecimal price;
        private Integer quantity;
        private String imageUrl;
        private Boolean inStock;
    }

    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .shortDescription(product.getShortDescription())
                .category(product.getCategory())
                .subcategory(product.getSubcategory())
                .tags(product.getTags())
                .price(product.getPrice())
                .compareAtPrice(product.getCompareAtPrice())
                .costPrice(product.getCostPrice())
                .minPrice(product.getMinPrice())
                .hasVariants(product.getHasVariants())
                .variantOptions(product.getVariantOptions())
                .trackInventory(product.getTrackInventory())
                .quantity(product.getQuantity())
                .allowBackorder(product.getAllowBackorder())
                .aiKeywords(product.getAiKeywords())
                .aiNotes(product.getAiNotes())
                .status(product.getStatus())
                .inStock(product.isInStock())
                .images(product.getImages() != null ? product.getImages().stream()
                        .map(ProductResponse::toImageResponse)
                        .collect(Collectors.toList()) : null)
                .variants(product.getVariants() != null ? product.getVariants().stream()
                        .map(ProductResponse::toVariantResponse)
                        .collect(Collectors.toList()) : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static ProductResponse fromEntityBasic(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .shortDescription(product.getShortDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .compareAtPrice(product.getCompareAtPrice())
                .status(product.getStatus())
                .inStock(product.isInStock())
                .quantity(product.getQuantity())
                .createdAt(product.getCreatedAt())
                .build();
    }

    private static ProductImageResponse toImageResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .url(image.getUrl())
                .altText(image.getAltText())
                .position(image.getPosition())
                .isMain(image.getIsMain())
                .build();
    }

    private static ProductVariantResponse toVariantResponse(ProductVariant variant) {
        return ProductVariantResponse.builder()
                .id(variant.getId())
                .name(variant.getName())
                .sku(variant.getSku())
                .options(variant.getOptions())
                .price(variant.getPrice())
                .quantity(variant.getQuantity())
                .imageUrl(variant.getImageUrl())
                .inStock(variant.isInStock())
                .build();
    }
}
