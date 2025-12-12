package com.invoiceng.controller;

import com.invoiceng.dto.request.CreateProductRequest;
import com.invoiceng.dto.request.UpdateProductRequest;
import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.dto.response.PaginatedResponse;
import com.invoiceng.dto.response.ProductResponse;
import com.invoiceng.security.CurrentUser;
import com.invoiceng.security.UserPrincipal;
import com.invoiceng.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management endpoints")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List products", description = "Get paginated list of products")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> listProducts(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        PaginatedResponse<ProductResponse> response = productService.listProducts(
                currentUser.getId(), category, status, page, limit, sortBy, sortOrder
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product", description = "Get product details by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        ProductResponse response = productService.getProduct(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name, description, or category")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam String q
    ) {
        List<ProductResponse> results = productService.searchProducts(currentUser.getId(), q);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get categories", description = "Get list of product categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories(
            @CurrentUser UserPrincipal currentUser
    ) {
        List<String> categories = productService.getCategories(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateProductRequest request
    ) {
        ProductResponse response = productService.createProduct(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Product created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update product details")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        ProductResponse response = productService.updateProduct(id, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        productService.deleteProduct(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }

    @PostMapping("/{id}/images")
    @Operation(summary = "Add product image", description = "Add an image to a product")
    public ResponseEntity<ApiResponse<ProductResponse>> addProductImage(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody CreateProductRequest.ProductImageRequest request
    ) {
        ProductResponse response = productService.addProductImage(id, currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Image added successfully"));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @Operation(summary = "Delete product image", description = "Delete an image from a product")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @PathVariable UUID imageId
    ) {
        productService.deleteProductImage(id, imageId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully"));
    }

    @PatchMapping("/{id}/inventory")
    @Operation(summary = "Update inventory", description = "Adjust product inventory quantity")
    public ResponseEntity<ApiResponse<ProductResponse>> updateInventory(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @RequestParam Integer adjustment
    ) {
        ProductResponse response = productService.updateInventory(id, currentUser.getId(), adjustment);
        return ResponseEntity.ok(ApiResponse.success(response, "Inventory updated successfully"));
    }
}
