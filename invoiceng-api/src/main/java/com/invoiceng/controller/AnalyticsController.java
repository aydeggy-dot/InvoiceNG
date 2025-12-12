package com.invoiceng.controller;

import com.invoiceng.dto.response.AnalyticsResponse;
import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.repository.ConversationMessageRepository;
import com.invoiceng.repository.ConversationRepository;
import com.invoiceng.repository.ProductRepository;
import com.invoiceng.repository.WhatsAppOrderRepository;
import com.invoiceng.security.CurrentUser;
import com.invoiceng.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "Business analytics and metrics endpoints")
public class AnalyticsController {

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository messageRepository;
    private final WhatsAppOrderRepository orderRepository;
    private final ProductRepository productRepository;

    @GetMapping
    @Operation(summary = "Get analytics", description = "Get comprehensive business analytics")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "30") int days
    ) {
        UUID businessId = currentUser.getId();
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        // Conversation metrics
        long totalConversations = conversationRepository.countByBusinessId(businessId);
        long activeConversations = conversationRepository.countActiveByBusinessId(businessId);
        long convertedConversations = conversationRepository.countConvertedByBusinessId(businessId);
        long abandonedConversations = conversationRepository.countAbandonedByBusinessId(businessId);
        long handoffConversations = conversationRepository.countHandedOffByBusinessId(businessId);

        BigDecimal conversionRate = BigDecimal.ZERO;
        if (totalConversations > 0) {
            conversionRate = BigDecimal.valueOf(convertedConversations)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalConversations), 2, RoundingMode.HALF_UP);
        }

        // Order metrics
        long totalOrders = orderRepository.countByBusinessId(businessId);
        long pendingOrders = orderRepository.countByBusinessIdAndPaymentStatus(businessId, "pending");
        long paidOrders = orderRepository.countByBusinessIdAndPaymentStatus(businessId, "paid");
        long shippedOrders = orderRepository.countByBusinessIdAndFulfillmentStatus(businessId, "shipped");
        long deliveredOrders = orderRepository.countByBusinessIdAndFulfillmentStatus(businessId, "delivered");
        long cancelledOrders = orderRepository.countByBusinessIdAndFulfillmentStatus(businessId, "cancelled");

        BigDecimal totalRevenue = orderRepository.sumTotalRevenue(businessId);
        BigDecimal averageOrderValue = orderRepository.avgOrderValue(businessId);

        // Message metrics
        long inboundMessages = messageRepository.countByBusinessIdAndDirectionSince(businessId, "inbound", since);
        long outboundMessages = messageRepository.countByBusinessIdAndDirectionSince(businessId, "outbound", since);
        long totalMessages = inboundMessages + outboundMessages;

        // Product metrics
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countActiveProducts(businessId);
        long outOfStockProducts = productRepository.countOutOfStockProducts(businessId);

        // Time-series data
        List<AnalyticsResponse.TimeSeriesData> revenueByDay = getRevenueByDay(businessId, since);
        List<AnalyticsResponse.TimeSeriesData> ordersByDay = getOrdersByDay(businessId, since);
        List<AnalyticsResponse.TimeSeriesData> conversationsByDay = getConversationsByDay(businessId, since);

        AnalyticsResponse response = AnalyticsResponse.builder()
                .totalConversations(totalConversations)
                .activeConversations(activeConversations)
                .convertedConversations(convertedConversations)
                .abandonedConversations(abandonedConversations)
                .handoffConversations(handoffConversations)
                .conversionRate(conversionRate)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .paidOrders(paidOrders)
                .shippedOrders(shippedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .averageOrderValue(averageOrderValue != null ? averageOrderValue : BigDecimal.ZERO)
                .totalMessages(totalMessages)
                .inboundMessages(inboundMessages)
                .outboundMessages(outboundMessages)
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .outOfStockProducts(outOfStockProducts)
                .revenueByDay(revenueByDay)
                .ordersByDay(ordersByDay)
                .conversationsByDay(conversationsByDay)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get quick summary", description = "Get a quick summary of key metrics")
    public ResponseEntity<ApiResponse<QuickSummary>> getQuickSummary(
            @CurrentUser UserPrincipal currentUser
    ) {
        UUID businessId = currentUser.getId();
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime thisWeek = LocalDateTime.now().minusDays(7);
        LocalDateTime thisMonth = LocalDateTime.now().minusDays(30);

        QuickSummary summary = new QuickSummary(
                conversationRepository.countByBusinessIdSince(businessId, today),
                conversationRepository.countByBusinessIdSince(businessId, thisWeek),
                orderRepository.countOrdersSince(businessId, today),
                orderRepository.countOrdersSince(businessId, thisWeek),
                orderRepository.sumRevenueSince(businessId, thisMonth),
                conversationRepository.countHandedOffByBusinessId(businessId)
        );

        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    private List<AnalyticsResponse.TimeSeriesData> getRevenueByDay(UUID businessId, LocalDateTime since) {
        List<Object[]> results = orderRepository.getRevenueByDay(businessId, since);
        List<AnalyticsResponse.TimeSeriesData> data = new ArrayList<>();

        for (Object[] row : results) {
            data.add(AnalyticsResponse.TimeSeriesData.builder()
                    .date(row[0] != null ? row[0].toString() : "")
                    .value(row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO)
                    .count(row[2] != null ? ((Number) row[2]).longValue() : 0L)
                    .build());
        }

        return data;
    }

    private List<AnalyticsResponse.TimeSeriesData> getOrdersByDay(UUID businessId, LocalDateTime since) {
        List<Object[]> results = orderRepository.getOrdersByDay(businessId, since);
        List<AnalyticsResponse.TimeSeriesData> data = new ArrayList<>();

        for (Object[] row : results) {
            data.add(AnalyticsResponse.TimeSeriesData.builder()
                    .date(row[0] != null ? row[0].toString() : "")
                    .count(row[1] != null ? ((Number) row[1]).longValue() : 0L)
                    .build());
        }

        return data;
    }

    private List<AnalyticsResponse.TimeSeriesData> getConversationsByDay(UUID businessId, LocalDateTime since) {
        List<Object[]> results = conversationRepository.getConversationsByDay(businessId, since);
        List<AnalyticsResponse.TimeSeriesData> data = new ArrayList<>();

        for (Object[] row : results) {
            data.add(AnalyticsResponse.TimeSeriesData.builder()
                    .date(row[0] != null ? row[0].toString() : "")
                    .count(row[1] != null ? ((Number) row[1]).longValue() : 0L)
                    .build());
        }

        return data;
    }

    public record QuickSummary(
            long conversationsToday,
            long conversationsThisWeek,
            long ordersToday,
            long ordersThisWeek,
            BigDecimal revenueThisMonth,
            long pendingHandoffs
    ) {}
}
