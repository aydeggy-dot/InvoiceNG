package com.invoiceng.service;

import com.invoiceng.dto.response.CustomerResponse;
import com.invoiceng.dto.response.DashboardResponse;
import com.invoiceng.dto.response.InvoiceResponse;
import com.invoiceng.entity.Invoice;
import com.invoiceng.entity.InvoiceStatus;
import com.invoiceng.entity.Payment;
import com.invoiceng.repository.InvoiceRepository;
import com.invoiceng.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerService customerService;

    public DashboardResponse getDashboardStats(UUID userId, String period) {
        LocalDateTime[] dateRange = getDateRange(period);
        LocalDateTime startDate = dateRange[0];
        LocalDateTime previousStartDate = dateRange[1];

        // Get current period stats
        DashboardResponse.DashboardOverview overview = getOverview(userId, startDate);

        // Get comparison with previous period
        DashboardResponse.DashboardComparison comparison = getComparison(userId, startDate, previousStartDate);

        // Get recent activity
        List<DashboardResponse.ActivityItem> recentActivity = getRecentActivity(userId, 10);

        return DashboardResponse.builder()
                .overview(overview)
                .comparison(comparison)
                .recentActivity(recentActivity)
                .build();
    }

    private DashboardResponse.DashboardOverview getOverview(UUID userId, LocalDateTime startDate) {
        BigDecimal totalRevenue = invoiceRepository.sumPaidAmountByUserIdSince(userId, startDate);
        BigDecimal pendingAmount = invoiceRepository.sumPendingAmountByUserIdSince(userId, startDate);
        BigDecimal overdueAmount = invoiceRepository.sumOverdueAmountByUserIdSince(userId, startDate);

        long totalInvoices = invoiceRepository.countInvoicesByUserIdSince(userId, startDate);
        long paidInvoices = invoiceRepository.countByUserIdAndStatusSince(userId, InvoiceStatus.PAID, startDate);
        long pendingInvoices = invoiceRepository.countByUserIdAndStatusSince(userId, InvoiceStatus.SENT, startDate)
                + invoiceRepository.countByUserIdAndStatusSince(userId, InvoiceStatus.VIEWED, startDate);
        long overdueInvoices = invoiceRepository.countByUserIdAndStatusSince(userId, InvoiceStatus.OVERDUE, startDate);

        // Calculate collection rate
        BigDecimal totalBilled = totalRevenue.add(pendingAmount).add(overdueAmount);
        double collectionRate = 0;
        if (totalBilled.compareTo(BigDecimal.ZERO) > 0) {
            collectionRate = totalRevenue.divide(totalBilled, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        return DashboardResponse.DashboardOverview.builder()
                .totalRevenue(totalRevenue)
                .totalInvoices(totalInvoices)
                .paidInvoices(paidInvoices)
                .pendingInvoices(pendingInvoices)
                .overdueInvoices(overdueInvoices)
                .pendingAmount(pendingAmount)
                .overdueAmount(overdueAmount)
                .collectionRate(Math.round(collectionRate * 10.0) / 10.0)
                .build();
    }

    private DashboardResponse.DashboardComparison getComparison(
            UUID userId,
            LocalDateTime currentStart,
            LocalDateTime previousStart
    ) {
        // Current period
        BigDecimal currentRevenue = invoiceRepository.sumPaidAmountByUserIdSince(userId, currentStart);
        long currentInvoices = invoiceRepository.countInvoicesByUserIdSince(userId, currentStart);

        // Previous period (use the same duration before currentStart)
        BigDecimal previousRevenue = invoiceRepository.sumPaidAmountByUserIdSince(userId, previousStart);
        previousRevenue = previousRevenue.subtract(currentRevenue); // Adjust for overlap
        if (previousRevenue.compareTo(BigDecimal.ZERO) < 0) {
            previousRevenue = BigDecimal.ZERO;
        }

        long previousInvoices = invoiceRepository.countInvoicesByUserIdSince(userId, previousStart) - currentInvoices;
        if (previousInvoices < 0) {
            previousInvoices = 0;
        }

        // Calculate percentage changes
        double revenueChange = calculatePercentageChange(previousRevenue, currentRevenue);
        double invoiceChange = calculatePercentageChange(
                BigDecimal.valueOf(previousInvoices),
                BigDecimal.valueOf(currentInvoices)
        );

        return DashboardResponse.DashboardComparison.builder()
                .revenueChange(revenueChange)
                .invoiceChange(invoiceChange)
                .build();
    }

    private List<DashboardResponse.ActivityItem> getRecentActivity(UUID userId, int limit) {
        List<DashboardResponse.ActivityItem> activities = new ArrayList<>();

        // Recent invoices
        List<Invoice> recentInvoices = invoiceRepository.findRecentByUserId(userId, limit);
        for (Invoice invoice : recentInvoices) {
            String type = switch (invoice.getStatus()) {
                case PAID -> "payment_received";
                case OVERDUE -> "invoice_overdue";
                default -> "invoice_created";
            };

            LocalDateTime timestamp = switch (invoice.getStatus()) {
                case PAID -> invoice.getPaidAt();
                default -> invoice.getCreatedAt();
            };

            activities.add(DashboardResponse.ActivityItem.builder()
                    .type(type)
                    .invoiceId(invoice.getId())
                    .invoiceNumber(invoice.getInvoiceNumber())
                    .customerName(invoice.getCustomer() != null ? invoice.getCustomer().getName() : "Unknown")
                    .amount(invoice.getTotal())
                    .timestamp(timestamp)
                    .build());
        }

        // Sort by timestamp and limit
        activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        if (activities.size() > limit) {
            activities = activities.subList(0, limit);
        }

        return activities;
    }

    public List<CustomerResponse> getTopCustomers(UUID userId, int limit) {
        return customerService.getTopCustomers(userId, limit);
    }

    public List<InvoiceResponse> getRecentInvoices(UUID userId, int limit) {
        List<Invoice> invoices = invoiceRepository.findRecentByUserId(userId, limit);
        return invoices.stream()
                .map(InvoiceResponse::fromEntity)
                .toList();
    }

    private LocalDateTime[] getDateRange(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime previousStartDate;

        switch (period != null ? period.toLowerCase() : "month") {
            case "week":
                startDate = now.minusWeeks(1);
                previousStartDate = now.minusWeeks(2);
                break;
            case "quarter":
                startDate = now.minusMonths(3);
                previousStartDate = now.minusMonths(6);
                break;
            case "year":
                startDate = now.minusYears(1);
                previousStartDate = now.minusYears(2);
                break;
            default: // month
                startDate = now.minusMonths(1);
                previousStartDate = now.minusMonths(2);
        }

        return new LocalDateTime[]{startDate, previousStartDate};
    }

    private double calculatePercentageChange(BigDecimal previous, BigDecimal current) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100 : 0;
        }

        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
