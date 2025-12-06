package com.invoiceng.controller;

import com.invoiceng.dto.request.CreateCustomerRequest;
import com.invoiceng.dto.request.UpdateCustomerRequest;
import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.dto.response.CustomerResponse;
import com.invoiceng.dto.response.PaginatedResponse;
import com.invoiceng.security.CurrentUser;
import com.invoiceng.security.UserPrincipal;
import com.invoiceng.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management endpoints")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "List customers", description = "Get paginated list of customers with optional search")
    public ResponseEntity<ApiResponse<PaginatedResponse<CustomerResponse>>> listCustomers(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        PaginatedResponse<CustomerResponse> response = customerService.listCustomers(
                currentUser.getId(), search, page, limit, sortBy, sortOrder
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer", description = "Get customer details by ID")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        CustomerResponse response = customerService.getCustomer(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create customer", description = "Create a new customer")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        CustomerResponse response = customerService.createCustomer(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Customer created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update customer details")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        CustomerResponse response = customerService.updateCustomer(id, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Customer updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Delete a customer")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        customerService.deleteCustomer(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully"));
    }
}
