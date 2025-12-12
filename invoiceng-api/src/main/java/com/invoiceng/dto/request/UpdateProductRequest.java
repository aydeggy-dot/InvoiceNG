package com.invoiceng.dto.request;

import jakarta.validation.constraints.DecimalMin;
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
public class UpdateProductRequest {

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
}
