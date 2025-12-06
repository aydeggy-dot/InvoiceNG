package com.invoiceng.controller;

import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.dto.response.CustomerResponse;
import com.invoiceng.dto.response.DashboardResponse;
import com.invoiceng.dto.response.InvoiceResponse;
import com.invoiceng.security.CurrentUser;
import com.invoiceng.security.UserPrincipal;
import com.invoiceng.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard stats", description = "Get overview statistics for the dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardStats(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "month") String period
    ) {
        DashboardResponse response = dashboardService.getDashboardStats(currentUser.getId(), period);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/top-customers")
    @Operation(summary = "Get top customers", description = "Get top customers by revenue")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getTopCustomers(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<CustomerResponse> response = dashboardService.getTopCustomers(currentUser.getId(), limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/recent-invoices")
    @Operation(summary = "Get recent invoices", description = "Get recent invoices for the dashboard")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getRecentInvoices(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<InvoiceResponse> response = dashboardService.getRecentInvoices(currentUser.getId(), limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
