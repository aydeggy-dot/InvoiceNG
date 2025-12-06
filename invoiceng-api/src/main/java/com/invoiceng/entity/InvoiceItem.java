package com.invoiceng.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String name;

    private String description;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal total;

    public void calculateTotal() {
        if (quantity != null && price != null) {
            this.total = price.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
