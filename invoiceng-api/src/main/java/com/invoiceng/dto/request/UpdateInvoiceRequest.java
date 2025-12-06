package com.invoiceng.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvoiceRequest {

    private UUID customerId;

    @Valid
    @Size(min = 1, message = "At least one item is required")
    private List<InvoiceItemRequest> items;

    @DecimalMin(value = "0", message = "Tax cannot be negative")
    private BigDecimal tax;

    @DecimalMin(value = "0", message = "Discount cannot be negative")
    private BigDecimal discount;

    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;

    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;

    @Size(max = 1000, message = "Terms must be less than 1000 characters")
    private String terms;
}
