package com.invoiceng.service;

import com.invoiceng.dto.request.CreateInvoiceRequest;
import com.invoiceng.dto.request.InvoiceItemRequest;
import com.invoiceng.dto.request.UpdateInvoiceRequest;
import com.invoiceng.dto.response.InvoiceListResponse;
import com.invoiceng.dto.response.InvoiceResponse;
import com.invoiceng.dto.response.PaginatedResponse;
import com.invoiceng.entity.*;
import com.invoiceng.exception.ResourceNotFoundException;
import com.invoiceng.exception.ValidationException;
import com.invoiceng.repository.CustomerRepository;
import com.invoiceng.repository.InvoiceRepository;
import com.invoiceng.repository.UserRepository;
import com.invoiceng.util.InvoiceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final InvoiceNumberGenerator invoiceNumberGenerator;
    private final PaystackService paystackService;

    public InvoiceListResponse listInvoices(
            UUID userId,
            InvoiceStatus status,
            UUID customerId,
            String search,
            int page,
            int limit,
            String sortBy,
            String sortOrder
    ) {
        Pageable pageable = createPageable(page, limit, sortBy, sortOrder);

        Page<Invoice> invoicePage;
        if (search != null && !search.isBlank()) {
            invoicePage = invoiceRepository.searchByUserIdAndInvoiceNumberOrCustomerName(userId, search.trim(), pageable);
        } else if (status != null) {
            invoicePage = invoiceRepository.findByUserIdAndStatus(userId, status, pageable);
        } else if (customerId != null) {
            invoicePage = invoiceRepository.findByUserIdAndCustomerId(userId, customerId, pageable);
        } else {
            invoicePage = invoiceRepository.findByUserId(userId, pageable);
        }

        User user = userRepository.findById(userId).orElse(null);
        String businessName = user != null ? user.getBusinessName() : null;

        List<InvoiceResponse> invoices = invoicePage.getContent().stream()
                .map(invoice -> InvoiceResponse.fromEntity(invoice, businessName))
                .toList();

        // Calculate summary
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        InvoiceListResponse.InvoiceSummary summary = InvoiceListResponse.InvoiceSummary.builder()
                .totalAmount(invoiceRepository.sumPaidAmountByUserIdSince(userId, startOfMonth)
                        .add(invoiceRepository.sumPendingAmountByUserIdSince(userId, startOfMonth))
                        .add(invoiceRepository.sumOverdueAmountByUserIdSince(userId, startOfMonth)))
                .paidAmount(invoiceRepository.sumPaidAmountByUserIdSince(userId, startOfMonth))
                .pendingAmount(invoiceRepository.sumPendingAmountByUserIdSince(userId, startOfMonth))
                .overdueAmount(invoiceRepository.sumOverdueAmountByUserIdSince(userId, startOfMonth))
                .totalCount(invoiceRepository.countInvoicesByUserIdSince(userId, startOfMonth))
                .paidCount(invoiceRepository.countByUserIdAndStatusSince(userId, InvoiceStatus.PAID, startOfMonth))
                .pendingCount(invoiceRepository.countByUserIdAndStatusSince(userId, InvoiceStatus.SENT, startOfMonth)
                        + invoiceRepository.countByUserIdAndStatusSince(userId, InvoiceStatus.VIEWED, startOfMonth))
                .overdueCount(invoiceRepository.countByUserIdAndStatusSince(userId, InvoiceStatus.OVERDUE, startOfMonth))
                .build();

        return InvoiceListResponse.builder()
                .data(invoices)
                .pagination(PaginatedResponse.PaginationInfo.builder()
                        .page(invoicePage.getNumber() + 1)
                        .limit(invoicePage.getSize())
                        .total(invoicePage.getTotalElements())
                        .totalPages(invoicePage.getTotalPages())
                        .hasNext(invoicePage.hasNext())
                        .hasPrevious(invoicePage.hasPrevious())
                        .build())
                .summary(summary)
                .build();
    }

    public InvoiceResponse getInvoice(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        User user = userRepository.findById(userId).orElse(null);
        String businessName = user != null ? user.getBusinessName() : null;

        return InvoiceResponse.fromEntity(invoice, businessName);
    }

    @Transactional
    public InvoiceResponse createInvoice(CreateInvoiceRequest request, UUID userId) {
        // Validate customer data
        if (request.getCustomerId() == null && request.getCustomerData() == null) {
            throw new ValidationException("Either customerId or customerData must be provided");
        }

        // Get or create customer
        Customer customer = customerService.getOrCreateCustomer(
                userId,
                request.getCustomerId(),
                request.getCustomerData()
        );

        // Create invoice items
        List<InvoiceItem> items = request.getItems().stream()
                .map(this::mapToInvoiceItem)
                .toList();

        // Calculate totals
        BigDecimal subtotal = items.stream()
                .map(InvoiceItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = request.getTax() != null ? request.getTax() : BigDecimal.ZERO;
        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax).subtract(discount);

        // Generate invoice number
        String invoiceNumber = invoiceNumberGenerator.generateInvoiceNumber(userId);

        // Create invoice
        User user = userRepository.getReferenceById(userId);
        Invoice invoice = Invoice.builder()
                .user(user)
                .customer(customer)
                .invoiceNumber(invoiceNumber)
                .items(items)
                .subtotal(subtotal)
                .tax(tax)
                .discount(discount)
                .total(total)
                .dueDate(request.getDueDate())
                .notes(request.getNotes())
                .terms(request.getTerms())
                .status(InvoiceStatus.DRAFT)
                .build();

        invoice = invoiceRepository.save(invoice);
        log.info("Created invoice {} for user {}", invoice.getId(), userId);

        // Update customer stats
        customer.incrementInvoiceCount();
        customer.addOutstanding(total);
        customerRepository.save(customer);

        // If sendImmediately, generate payment link
        if (request.isSendImmediately()) {
            invoice = generatePaymentLinkAndSend(invoice, user);
        }

        return InvoiceResponse.fromEntity(invoice, user.getBusinessName());
    }

    @Transactional
    public InvoiceResponse updateInvoice(UUID invoiceId, UpdateInvoiceRequest request, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        if (!invoice.isEditable()) {
            throw new ValidationException("Cannot edit invoice that has been sent or paid");
        }

        // Update customer if provided
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findByIdAndUserId(request.getCustomerId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));
            invoice.setCustomer(customer);
        }

        // Update items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<InvoiceItem> items = request.getItems().stream()
                    .map(this::mapToInvoiceItem)
                    .toList();
            invoice.setItems(items);
        }

        // Update other fields
        if (request.getTax() != null) {
            invoice.setTax(request.getTax());
        }
        if (request.getDiscount() != null) {
            invoice.setDiscount(request.getDiscount());
        }
        if (request.getDueDate() != null) {
            invoice.setDueDate(request.getDueDate());
        }
        if (request.getNotes() != null) {
            invoice.setNotes(request.getNotes());
        }
        if (request.getTerms() != null) {
            invoice.setTerms(request.getTerms());
        }

        // Recalculate totals
        invoice.calculateTotals();

        invoice = invoiceRepository.save(invoice);
        log.info("Updated invoice {}", invoiceId);

        User user = userRepository.findById(userId).orElse(null);
        return InvoiceResponse.fromEntity(invoice, user != null ? user.getBusinessName() : null);
    }

    @Transactional
    public void deleteInvoice(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        if (!invoice.isDraft()) {
            throw new ValidationException("Cannot delete invoice that has been sent or paid");
        }

        invoiceRepository.delete(invoice);
        log.info("Deleted invoice {}", invoiceId);
    }

    @Transactional
    public InvoiceResponse sendInvoice(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        if (invoice.isPaid()) {
            throw new ValidationException("Invoice has already been paid");
        }

        if (invoice.isCancelled()) {
            throw new ValidationException("Cannot send a cancelled invoice");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        invoice = generatePaymentLinkAndSend(invoice, user);

        return InvoiceResponse.fromEntity(invoice, user.getBusinessName());
    }

    @Transactional
    public InvoiceResponse cancelInvoice(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        if (invoice.isPaid()) {
            throw new ValidationException("Cannot cancel a paid invoice");
        }

        invoice.cancel();
        invoice = invoiceRepository.save(invoice);
        log.info("Cancelled invoice {}", invoiceId);

        // Update customer outstanding amount
        if (invoice.getCustomer() != null) {
            Customer customer = invoice.getCustomer();
            customer.setTotalOutstanding(customer.getTotalOutstanding().subtract(invoice.getTotal()));
            if (customer.getTotalOutstanding().compareTo(BigDecimal.ZERO) < 0) {
                customer.setTotalOutstanding(BigDecimal.ZERO);
            }
            customerRepository.save(customer);
        }

        User user = userRepository.findById(userId).orElse(null);
        return InvoiceResponse.fromEntity(invoice, user != null ? user.getBusinessName() : null);
    }

    @Transactional
    public InvoiceResponse duplicateInvoice(UUID invoiceId, UUID userId) {
        Invoice original = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        String newInvoiceNumber = invoiceNumberGenerator.generateInvoiceNumber(userId);

        Invoice duplicate = Invoice.builder()
                .user(original.getUser())
                .customer(original.getCustomer())
                .invoiceNumber(newInvoiceNumber)
                .items(original.getItems())
                .subtotal(original.getSubtotal())
                .tax(original.getTax())
                .discount(original.getDiscount())
                .total(original.getTotal())
                .dueDate(original.getDueDate())
                .notes(original.getNotes())
                .terms(original.getTerms())
                .status(InvoiceStatus.DRAFT)
                .build();

        duplicate = invoiceRepository.save(duplicate);
        log.info("Duplicated invoice {} to {}", invoiceId, duplicate.getId());

        User user = userRepository.findById(userId).orElse(null);
        return InvoiceResponse.fromEntity(duplicate, user != null ? user.getBusinessName() : null);
    }

    public Invoice getInvoiceByPaymentRef(String paymentRef) {
        return invoiceRepository.findByPaymentRef(paymentRef)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "paymentRef", paymentRef));
    }

    private Invoice generatePaymentLinkAndSend(Invoice invoice, User user) {
        // Generate payment reference
        String paymentRef = invoiceNumberGenerator.generatePaymentReference(invoice.getId());
        invoice.setPaymentRef(paymentRef);

        // Get customer email (use placeholder if not available)
        String customerEmail = invoice.getCustomer() != null && invoice.getCustomer().getEmail() != null
                ? invoice.getCustomer().getEmail()
                : "customer@invoiceng.com";

        String customerName = invoice.getCustomer() != null
                ? invoice.getCustomer().getName()
                : "Customer";

        // Try to initialize Paystack payment, but don't fail if it doesn't work
        try {
            PaystackService.PaystackInitResponse paystackResponse = paystackService.initializeTransaction(
                    paymentRef,
                    invoice.getTotal(),
                    customerEmail,
                    customerName,
                    invoice.getId()
            );

            invoice.setPaymentLink(paystackResponse.getAuthorizationUrl());
            invoice.setPaystackAccessCode(paystackResponse.getAccessCode());
        } catch (Exception e) {
            log.warn("Failed to initialize Paystack payment for invoice {}: {}. Invoice will be sent without payment link.",
                    invoice.getId(), e.getMessage());
            // Set a placeholder payment link for development
            invoice.setPaymentLink("https://paystack.com/pay/" + paymentRef);
        }

        invoice.markAsSent();

        return invoiceRepository.save(invoice);
    }

    private InvoiceItem mapToInvoiceItem(InvoiceItemRequest request) {
        InvoiceItem item = InvoiceItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .build();
        item.calculateTotal();
        return item;
    }

    private Pageable createPageable(int page, int limit, String sortBy, String sortOrder) {
        page = Math.max(1, page) - 1;
        limit = Math.min(Math.max(1, limit), 100);

        String sortField = switch (sortBy != null ? sortBy.toLowerCase() : "createdat") {
            case "invoicenumber" -> "invoiceNumber";
            case "duedate" -> "dueDate";
            case "total" -> "total";
            case "status" -> "status";
            default -> "createdAt";
        };

        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(page, limit, Sort.by(direction, sortField));
    }
}
