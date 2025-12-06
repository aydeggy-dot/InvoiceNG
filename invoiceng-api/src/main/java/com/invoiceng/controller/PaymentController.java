package com.invoiceng.controller;

import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.entity.Payment;
import com.invoiceng.security.CurrentUser;
import com.invoiceng.security.UserPrincipal;
import com.invoiceng.service.PaymentService;
import com.invoiceng.service.PaystackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initialize")
    @Operation(summary = "Initialize payment", description = "Initialize payment for an invoice")
    public ResponseEntity<ApiResponse<Map<String, Object>>> initializePayment(
            @CurrentUser UserPrincipal currentUser,
            @RequestBody Map<String, UUID> request
    ) {
        UUID invoiceId = request.get("invoiceId");
        Payment payment = paymentService.initializePayment(invoiceId, currentUser.getId());

        Map<String, Object> response = Map.of(
                "reference", payment.getReference(),
                "invoiceId", payment.getInvoice().getId(),
                "amount", payment.getAmount()
        );

        return ResponseEntity.ok(ApiResponse.success(response, "Payment initialized"));
    }

    @GetMapping("/verify/{reference}")
    @Operation(summary = "Verify payment", description = "Verify payment status")
    public ResponseEntity<ApiResponse<PaystackService.PaystackVerifyResponse>> verifyPayment(
            @PathVariable String reference
    ) {
        PaystackService.PaystackVerifyResponse response = paymentService.verifyPayment(reference);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
