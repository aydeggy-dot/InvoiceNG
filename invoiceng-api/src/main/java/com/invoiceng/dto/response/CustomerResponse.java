package com.invoiceng.dto.response;

import com.invoiceng.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String notes;
    private Integer paymentScore;
    private Integer totalInvoices;
    private BigDecimal totalPaid;
    private BigDecimal totalOutstanding;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CustomerResponse fromEntity(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .notes(customer.getNotes())
                .paymentScore(customer.getPaymentScore())
                .totalInvoices(customer.getTotalInvoices())
                .totalPaid(customer.getTotalPaid())
                .totalOutstanding(customer.getTotalOutstanding())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
