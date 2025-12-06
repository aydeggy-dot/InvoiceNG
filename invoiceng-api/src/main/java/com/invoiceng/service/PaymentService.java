package com.invoiceng.service;

import com.invoiceng.entity.Customer;
import com.invoiceng.entity.Invoice;
import com.invoiceng.entity.Payment;
import com.invoiceng.exception.PaymentException;
import com.invoiceng.exception.ResourceNotFoundException;
import com.invoiceng.repository.CustomerRepository;
import com.invoiceng.repository.InvoiceRepository;
import com.invoiceng.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final PaystackService paystackService;

    @Transactional
    public Payment initializePayment(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        if (invoice.isPaid()) {
            throw new PaymentException("Invoice has already been paid");
        }

        if (invoice.isCancelled()) {
            throw new PaymentException("Cannot pay a cancelled invoice");
        }

        // Create payment record
        String reference = invoice.getPaymentRef();
        if (reference == null) {
            throw new PaymentException("Invoice has no payment reference. Please send the invoice first.");
        }

        // Check if payment already exists
        if (paymentRepository.existsByReference(reference)) {
            throw new PaymentException("Payment already initialized for this invoice");
        }

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(invoice.getTotal())
                .reference(reference)
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public PaystackService.PaystackVerifyResponse verifyPayment(String reference) {
        return paystackService.verifyTransaction(reference);
    }

    @Transactional
    public void handleSuccessfulPayment(Map<String, Object> webhookData) {
        String reference = (String) webhookData.get("reference");
        Integer amountInKobo = (Integer) webhookData.get("amount");
        String channel = (String) webhookData.get("channel");
        String paidAtStr = (String) webhookData.get("paid_at");
        String paystackRef = String.valueOf(webhookData.get("id"));

        log.info("Processing successful payment: ref={}, amount={}", reference, amountInKobo);

        // Find invoice by payment reference
        Invoice invoice = invoiceRepository.findByPaymentRef(reference)
                .orElseThrow(() -> {
                    log.error("Invoice not found for payment ref: {}", reference);
                    return new ResourceNotFoundException("Invoice", "paymentRef", reference);
                });

        if (invoice.isPaid()) {
            log.warn("Invoice {} already marked as paid", invoice.getId());
            return;
        }

        // Parse paid_at
        LocalDateTime paidAt = LocalDateTime.now();
        if (paidAtStr != null) {
            try {
                paidAt = LocalDateTime.parse(paidAtStr.replace("Z", ""));
            } catch (Exception e) {
                log.warn("Could not parse paid_at: {}", paidAtStr);
            }
        }

        // Create or update payment record
        BigDecimal amount = amountInKobo != null
                ? BigDecimal.valueOf(amountInKobo / 100.0)
                : invoice.getTotal();

        Payment payment = paymentRepository.findByReference(reference)
                .orElse(Payment.builder()
                        .invoice(invoice)
                        .amount(amount)
                        .reference(reference)
                        .build());

        payment.markAsSuccessful(paystackRef, channel, paidAt);
        paymentRepository.save(payment);

        // Update invoice status
        invoice.markAsPaid();
        invoiceRepository.save(invoice);

        // Update customer stats
        Customer customer = invoice.getCustomer();
        if (customer != null) {
            customer.addPayment(amount);
            customerRepository.save(customer);
        }

        log.info("Payment processed successfully: invoice={}, amount={}", invoice.getId(), amount);
    }

    @Transactional
    public void handleFailedPayment(Map<String, Object> webhookData) {
        String reference = (String) webhookData.get("reference");
        log.info("Processing failed payment: ref={}", reference);

        Payment payment = paymentRepository.findByReference(reference).orElse(null);
        if (payment != null) {
            payment.markAsFailed();
            paymentRepository.save(payment);
        }
    }
}
