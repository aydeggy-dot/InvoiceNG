package com.invoiceng.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private DashboardOverview overview;
    private DashboardComparison comparison;
    private List<ActivityItem> recentActivity;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardOverview {
        private BigDecimal totalRevenue;
        private long totalInvoices;
        private long paidInvoices;
        private long pendingInvoices;
        private long overdueInvoices;
        private BigDecimal pendingAmount;
        private BigDecimal overdueAmount;
        private double collectionRate; // Percentage
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardComparison {
        private double revenueChange; // Percentage change vs previous period
        private double invoiceChange;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityItem {
        private String type; // invoice_created, payment_received, invoice_overdue
        private UUID invoiceId;
        private String invoiceNumber;
        private String customerName;
        private BigDecimal amount;
        private LocalDateTime timestamp;
    }
}
