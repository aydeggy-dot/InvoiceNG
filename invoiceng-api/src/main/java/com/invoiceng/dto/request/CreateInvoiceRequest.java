package com.invoiceng.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
public class CreateInvoiceRequest {

    // Either customerId OR customerData must be provided
    private UUID customerId;

    @Valid
    private CreateCustomerRequest customerData;

    @NotNull(message = "Items are required")
    @Size(min = 1, message = "At least one item is required")
    @Valid
    private List<InvoiceItemRequest> items;

    @DecimalMin(value = "0", message = "Tax cannot be negative")
    private BigDecimal tax;

    @DecimalMin(value = "0", message = "Discount cannot be negative")
    private BigDecimal discount;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;

    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;

    @Size(max = 1000, message = "Terms must be less than 1000 characters")
    private String terms;

    private boolean sendImmediately;
}
