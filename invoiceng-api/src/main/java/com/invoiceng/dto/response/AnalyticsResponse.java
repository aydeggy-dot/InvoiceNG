package com.invoiceng.dto.response;

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
public class AnalyticsResponse {

    // Conversation metrics
    private Long totalConversations;
    private Long activeConversations;
    private Long convertedConversations;
    private Long abandonedConversations;
    private Long handoffConversations;
    private BigDecimal conversionRate;

    // Order metrics
    private Long totalOrders;
    private Long pendingOrders;
    private Long paidOrders;
    private Long shippedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;

    // Message metrics
    private Long totalMessages;
    private Long inboundMessages;
    private Long outboundMessages;

    // Product metrics
    private Long totalProducts;
    private Long activeProducts;
    private Long outOfStockProducts;

    // Time-series data for charts
    private List<TimeSeriesData> revenueByDay;
    private List<TimeSeriesData> ordersByDay;
    private List<TimeSeriesData> conversationsByDay;

    // Top products
    private List<TopProductData> topProducts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSeriesData {
        private String date;
        private BigDecimal value;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductData {
        private String productName;
        private Long orderCount;
        private BigDecimal revenue;
    }
}
