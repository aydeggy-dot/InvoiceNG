package com.invoiceng.dto.response;

import com.invoiceng.entity.Invoice;
import com.invoiceng.entity.InvoiceItem;
import com.invoiceng.entity.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private UUID id;
    private String invoiceNumber;
    private CustomerSummary customer;
    private List<InvoiceItem> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
    private InvoiceStatus status;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String notes;
    private String terms;
    private String paymentRef;
    private String paymentLink;
    private String pdfUrl;
    private LocalDateTime sentAt;
    private LocalDateTime viewedAt;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Pre-formatted WhatsApp message
    private String whatsappMessage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerSummary {
        private UUID id;
        private String name;
        private String phone;
        private String email;
    }

    public static InvoiceResponse fromEntity(Invoice invoice) {
        return fromEntity(invoice, null);
    }

    public static InvoiceResponse fromEntity(Invoice invoice, String businessName) {
        CustomerSummary customerSummary = null;
        if (invoice.getCustomer() != null) {
            customerSummary = CustomerSummary.builder()
                    .id(invoice.getCustomer().getId())
                    .name(invoice.getCustomer().getName())
                    .phone(invoice.getCustomer().getPhone())
                    .email(invoice.getCustomer().getEmail())
                    .build();
        }

        InvoiceResponse response = InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customer(customerSummary)
                .items(invoice.getItems())
                .subtotal(invoice.getSubtotal())
                .tax(invoice.getTax())
                .discount(invoice.getDiscount())
                .total(invoice.getTotal())
                .status(invoice.getStatus())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .notes(invoice.getNotes())
                .terms(invoice.getTerms())
                .paymentRef(invoice.getPaymentRef())
                .paymentLink(invoice.getPaymentLink())
                .pdfUrl(invoice.getPdfUrl())
                .sentAt(invoice.getSentAt())
                .viewedAt(invoice.getViewedAt())
                .paidAt(invoice.getPaidAt())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();

        // Generate WhatsApp message if payment link exists
        if (invoice.getPaymentLink() != null && customerSummary != null) {
            response.setWhatsappMessage(generateWhatsAppMessage(invoice, businessName));
        }

        return response;
    }

    private static String generateWhatsAppMessage(Invoice invoice, String businessName) {
        String business = businessName != null ? businessName : "InvoiceNG";
        String customerName = invoice.getCustomer() != null ? invoice.getCustomer().getName() : "Customer";

        return String.format("""
                📄 *Invoice #%s*

                Hello %s,

                Please find your invoice details below:

                💰 *Amount Due:* ₦%,.2f
                📅 *Due Date:* %s

                Pay securely here:
                %s

                Thank you for your business!

                — %s""",
                invoice.getInvoiceNumber(),
                customerName,
                invoice.getTotal(),
                invoice.getDueDate().toString(),
                invoice.getPaymentLink(),
                business
        );
    }
}
