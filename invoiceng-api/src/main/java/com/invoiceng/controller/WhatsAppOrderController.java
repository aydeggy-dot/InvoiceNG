package com.invoiceng.controller;

import com.invoiceng.dto.request.CreateWhatsAppOrderRequest;
import com.invoiceng.dto.request.UpdateWhatsAppOrderRequest;
import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.dto.response.PaginatedResponse;
import com.invoiceng.dto.response.WhatsAppOrderResponse;
import com.invoiceng.security.CurrentUser;
import com.invoiceng.security.UserPrincipal;
import com.invoiceng.service.WhatsAppOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/whatsapp-orders")
@RequiredArgsConstructor
@Tag(name = "WhatsApp Orders", description = "WhatsApp order management endpoints")
public class WhatsAppOrderController {

    private final WhatsAppOrderService orderService;

    @GetMapping
    @Operation(summary = "List orders", description = "Get paginated list of WhatsApp orders")
    public ResponseEntity<ApiResponse<PaginatedResponse<WhatsAppOrderResponse>>> listOrders(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String fulfillmentStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        PaginatedResponse<WhatsAppOrderResponse> response = orderService.listOrders(
                currentUser.getId(), paymentStatus, fulfillmentStatus, page, limit, sortBy, sortOrder
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order", description = "Get order details by ID")
    public ResponseEntity<ApiResponse<WhatsAppOrderResponse>> getOrder(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        WhatsAppOrderResponse response = orderService.getOrder(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/by-number/{orderNumber}")
    @Operation(summary = "Get order by number", description = "Get order details by order number")
    public ResponseEntity<ApiResponse<WhatsAppOrderResponse>> getOrderByNumber(
            @PathVariable String orderNumber
    ) {
        WhatsAppOrderResponse response = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create order", description = "Create a new WhatsApp order")
    public ResponseEntity<ApiResponse<WhatsAppOrderResponse>> createOrder(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateWhatsAppOrderRequest request
    ) {
        WhatsAppOrderResponse response = orderService.createOrder(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Order created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order", description = "Update order details")
    public ResponseEntity<ApiResponse<WhatsAppOrderResponse>> updateOrder(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWhatsAppOrderRequest request
    ) {
        WhatsAppOrderResponse response = orderService.updateOrder(id, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Order updated successfully"));
    }

    @PostMapping("/{id}/mark-paid")
    @Operation(summary = "Mark order as paid", description = "Mark order payment as completed")
    public ResponseEntity<ApiResponse<WhatsAppOrderResponse>> markAsPaid(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @RequestParam(required = false) String paymentReference,
            @RequestParam(required = false) String paymentMethod
    ) {
        WhatsAppOrderResponse response = orderService.markAsPaid(id, currentUser.getId(), paymentReference, paymentMethod);
        return ResponseEntity.ok(ApiResponse.success(response, "Order marked as paid"));
    }

    @PostMapping("/{id}/ship")
    @Operation(summary = "Ship order", description = "Mark order as shipped")
    public ResponseEntity<ApiResponse<WhatsAppOrderResponse>> shipOrder(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @RequestParam(required = false) String trackingNumber
    ) {
        WhatsAppOrderResponse response = orderService.markAsShipped(id, currentUser.getId(), trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(response, "Order marked as shipped"));
    }

    @PostMapping("/{id}/deliver")
    @Operation(summary = "Deliver order", description = "Mark order as delivered")
    public ResponseEntity<ApiResponse<WhatsAppOrderResponse>> deliverOrder(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        WhatsAppOrderResponse response = orderService.markAsDelivered(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Order marked as delivered"));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order")
    public ResponseEntity<ApiResponse<WhatsAppOrderResponse>> cancelOrder(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        WhatsAppOrderResponse response = orderService.cancelOrder(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Order cancelled"));
    }
}
