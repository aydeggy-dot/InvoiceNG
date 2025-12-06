package com.invoiceng.util;

import com.invoiceng.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvoiceNumberGenerator {

    private final InvoiceRepository invoiceRepository;

    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * Generate invoice number in format: INV-YYYYMM-XXXXX
     * Example: INV-202501-00001
     * Note: Invoice numbers are globally unique across all users
     */
    public String generateInvoiceNumber(UUID userId) {
        String yearMonth = LocalDateTime.now().format(YEAR_MONTH_FORMAT);
        String prefix = "INV-" + yearMonth + "-%";

        // Find the max sequence globally for this month (not per-user)
        int sequence = invoiceRepository.findMaxInvoiceSequence(prefix)
                .orElse(0) + 1;

        return String.format("INV-%s-%05d", yearMonth, sequence);
    }

    /**
     * Generate a unique payment reference
     * Format: PAY-{invoiceId}-{timestamp}
     */
    public String generatePaymentReference(UUID invoiceId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortId = invoiceId.toString().substring(0, 8);
        return String.format("PAY-%s-%s", shortId, timestamp);
    }
}
