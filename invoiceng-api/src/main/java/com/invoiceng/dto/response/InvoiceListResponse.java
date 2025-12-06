package com.invoiceng.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceListResponse {

    private List<InvoiceResponse> data;
    private PaginatedResponse.PaginationInfo pagination;
    private InvoiceSummary summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceSummary {
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal pendingAmount;
        private BigDecimal overdueAmount;
        private long totalCount;
        private long paidCount;
        private long pendingCount;
        private long overdueCount;
    }
}
