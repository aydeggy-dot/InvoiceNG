package com.invoiceng.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;

    private String description;

    @Size(max = 500, message = "Short description must be less than 500 characters")
    private String shortDescription;

    @Size(max = 100, message = "Category must be less than 100 characters")
    private String category;

    @Size(max = 100, message = "Subcategory must be less than 100 characters")
    private String subcategory;

    private List<String> tags;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
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

    private List<ProductImageRequest> images;

    private List<ProductVariantRequest> variants;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageRequest {
        @NotBlank(message = "Image URL is required")
        private String url;
        private String altText;
        private Integer position;
        private Boolean isMain;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantRequest {
        @NotBlank(message = "Variant name is required")
        private String name;
        private String sku;
        private Map<String, Object> options;
        private BigDecimal price;
        private Integer quantity;
        private String imageUrl;
    }
}
