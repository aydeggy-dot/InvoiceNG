package com.invoiceng.controller;

import com.invoiceng.dto.request.CreateInvoiceRequest;
import com.invoiceng.dto.request.UpdateInvoiceRequest;
import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.dto.response.InvoiceListResponse;
import com.invoiceng.dto.response.InvoiceResponse;
import com.invoiceng.entity.InvoiceStatus;
import com.invoiceng.security.CurrentUser;
import com.invoiceng.security.UserPrincipal;
import com.invoiceng.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Invoice management endpoints")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @Operation(summary = "List invoices", description = "Get paginated list of invoices with optional filters")
    public ResponseEntity<ApiResponse<InvoiceListResponse>> listInvoices(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        InvoiceStatus invoiceStatus = status != null ? InvoiceStatus.fromValue(status) : null;
        InvoiceListResponse response = invoiceService.listInvoices(
                currentUser.getId(), invoiceStatus, customerId, search, page, limit, sortBy, sortOrder
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice", description = "Get invoice details by ID")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoice(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        InvoiceResponse response = invoiceService.getInvoice(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create invoice", description = "Create a new invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateInvoiceRequest request
    ) {
        InvoiceResponse response = invoiceService.createInvoice(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Invoice created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update invoice", description = "Update a draft invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> updateInvoice(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInvoiceRequest request
    ) {
        InvoiceResponse response = invoiceService.updateInvoice(id, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Invoice updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete invoice", description = "Delete a draft invoice")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        invoiceService.deleteInvoice(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Invoice deleted successfully"));
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "Send invoice", description = "Mark invoice as sent and generate payment link")
    public ResponseEntity<ApiResponse<InvoiceResponse>> sendInvoice(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        InvoiceResponse response = invoiceService.sendInvoice(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Invoice sent successfully"));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel invoice", description = "Cancel an invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> cancelInvoice(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        InvoiceResponse response = invoiceService.cancelInvoice(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Invoice cancelled"));
    }

    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Duplicate invoice", description = "Create a copy of an invoice as a new draft")
    public ResponseEntity<ApiResponse<InvoiceResponse>> duplicateInvoice(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        InvoiceResponse response = invoiceService.duplicateInvoice(id, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Invoice duplicated successfully"));
    }
}
