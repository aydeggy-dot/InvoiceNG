package com.invoiceng.service;

import com.invoiceng.dto.request.CreateProductRequest;
import com.invoiceng.dto.request.UpdateProductRequest;
import com.invoiceng.dto.response.PaginatedResponse;
import com.invoiceng.dto.response.ProductResponse;
import com.invoiceng.entity.Product;
import com.invoiceng.entity.ProductImage;
import com.invoiceng.entity.ProductVariant;
import com.invoiceng.entity.User;
import com.invoiceng.exception.ResourceNotFoundException;
import com.invoiceng.repository.ProductImageRepository;
import com.invoiceng.repository.ProductRepository;
import com.invoiceng.repository.ProductVariantRepository;
import com.invoiceng.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final UserRepository userRepository;

    public PaginatedResponse<ProductResponse> listProducts(
            UUID businessId,
            String category,
            String status,
            int page,
            int limit,
            String sortBy,
            String sortOrder
    ) {
        Pageable pageable = createPageable(page, limit, sortBy, sortOrder);

        Page<Product> productPage;
        if (category != null && !category.isBlank()) {
            productPage = productRepository.findByBusinessIdAndCategory(businessId, category, pageable);
        } else if (status != null && !status.isBlank()) {
            productPage = productRepository.findByBusinessIdAndStatus(businessId, status, pageable);
        } else {
            productPage = productRepository.findByBusinessId(businessId, pageable);
        }

        return PaginatedResponse.fromPage(productPage, ProductResponse::fromEntityBasic);
    }

    public ProductResponse getProduct(UUID productId, UUID businessId) {
        Product product = productRepository.findByIdAndBusinessId(productId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return ProductResponse.fromEntity(product);
    }

    public List<ProductResponse> searchProducts(UUID businessId, String query) {
        return productRepository.searchProducts(businessId, query)
                .stream()
                .map(ProductResponse::fromEntityBasic)
                .toList();
    }

    public List<String> getCategories(UUID businessId) {
        return productRepository.findCategoriesByBusinessId(businessId);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, UUID businessId) {
        User business = userRepository.getReferenceById(businessId);

        Product product = Product.builder()
                .business(business)
                .name(request.getName().trim())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .category(request.getCategory())
                .subcategory(request.getSubcategory())
                .tags(request.getTags())
                .price(request.getPrice())
                .compareAtPrice(request.getCompareAtPrice())
                .costPrice(request.getCostPrice())
                .minPrice(request.getMinPrice())
                .hasVariants(request.getHasVariants() != null ? request.getHasVariants() : false)
                .variantOptions(request.getVariantOptions())
                .trackInventory(request.getTrackInventory() != null ? request.getTrackInventory() : false)
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0)
                .allowBackorder(request.getAllowBackorder() != null ? request.getAllowBackorder() : false)
                .aiKeywords(request.getAiKeywords())
                .aiNotes(request.getAiNotes())
                .status(request.getStatus() != null ? request.getStatus() : "active")
                .build();

        product = productRepository.save(product);

        // Create images
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<ProductImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImages().size(); i++) {
                CreateProductRequest.ProductImageRequest imageReq = request.getImages().get(i);
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .url(imageReq.getUrl())
                        .altText(imageReq.getAltText())
                        .position(imageReq.getPosition() != null ? imageReq.getPosition() : i)
                        .isMain(imageReq.getIsMain() != null ? imageReq.getIsMain() : (i == 0))
                        .build();
                images.add(image);
            }
            imageRepository.saveAll(images);
            product.setImages(images);
        }

        // Create variants
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            List<ProductVariant> variants = new ArrayList<>();
            for (CreateProductRequest.ProductVariantRequest variantReq : request.getVariants()) {
                ProductVariant variant = ProductVariant.builder()
                        .product(product)
                        .name(variantReq.getName())
                        .sku(variantReq.getSku())
                        .options(variantReq.getOptions() != null ? variantReq.getOptions() : new HashMap<>())
                        .price(variantReq.getPrice())
                        .quantity(variantReq.getQuantity() != null ? variantReq.getQuantity() : 0)
                        .imageUrl(variantReq.getImageUrl())
                        .build();
                variants.add(variant);
            }
            variantRepository.saveAll(variants);
            product.setVariants(variants);
        }

        log.info("Created product {} for business {}", product.getId(), businessId);
        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request, UUID businessId) {
        Product product = productRepository.findByIdAndBusinessId(productId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (request.getName() != null) {
            product.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getShortDescription() != null) {
            product.setShortDescription(request.getShortDescription());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getSubcategory() != null) {
            product.setSubcategory(request.getSubcategory());
        }
        if (request.getTags() != null) {
            product.setTags(request.getTags());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getCompareAtPrice() != null) {
            product.setCompareAtPrice(request.getCompareAtPrice());
        }
        if (request.getCostPrice() != null) {
            product.setCostPrice(request.getCostPrice());
        }
        if (request.getMinPrice() != null) {
            product.setMinPrice(request.getMinPrice());
        }
        if (request.getHasVariants() != null) {
            product.setHasVariants(request.getHasVariants());
        }
        if (request.getVariantOptions() != null) {
            product.setVariantOptions(request.getVariantOptions());
        }
        if (request.getTrackInventory() != null) {
            product.setTrackInventory(request.getTrackInventory());
        }
        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }
        if (request.getAllowBackorder() != null) {
            product.setAllowBackorder(request.getAllowBackorder());
        }
        if (request.getAiKeywords() != null) {
            product.setAiKeywords(request.getAiKeywords());
        }
        if (request.getAiNotes() != null) {
            product.setAiNotes(request.getAiNotes());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        product = productRepository.save(product);
        log.info("Updated product {}", productId);

        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public void deleteProduct(UUID productId, UUID businessId) {
        Product product = productRepository.findByIdAndBusinessId(productId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        productRepository.delete(product);
        log.info("Deleted product {}", productId);
    }

    @Transactional
    public ProductResponse addProductImage(UUID productId, UUID businessId, CreateProductRequest.ProductImageRequest imageRequest) {
        Product product = productRepository.findByIdAndBusinessId(productId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (Boolean.TRUE.equals(imageRequest.getIsMain())) {
            imageRepository.clearMainImage(productId);
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .url(imageRequest.getUrl())
                .altText(imageRequest.getAltText())
                .position(imageRequest.getPosition() != null ? imageRequest.getPosition() : 0)
                .isMain(imageRequest.getIsMain() != null ? imageRequest.getIsMain() : false)
                .build();

        imageRepository.save(image);
        log.info("Added image to product {}", productId);

        return getProduct(productId, businessId);
    }

    @Transactional
    public void deleteProductImage(UUID productId, UUID imageId, UUID businessId) {
        Product product = productRepository.findByIdAndBusinessId(productId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        ProductImage image = imageRepository.findById(imageId)
                .filter(i -> i.getProduct().getId().equals(productId))
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));

        imageRepository.delete(image);
        log.info("Deleted image {} from product {}", imageId, productId);
    }

    @Transactional
    public ProductResponse updateInventory(UUID productId, UUID businessId, Integer quantityChange) {
        Product product = productRepository.findByIdAndBusinessId(productId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        int newQuantity = (product.getQuantity() != null ? product.getQuantity() : 0) + quantityChange;
        product.setQuantity(Math.max(0, newQuantity));

        product = productRepository.save(product);
        log.info("Updated inventory for product {}: {} -> {}", productId, product.getQuantity() - quantityChange, newQuantity);

        return ProductResponse.fromEntity(product);
    }

    public long countActiveProducts(UUID businessId) {
        return productRepository.countActiveProducts(businessId);
    }

    public long countOutOfStockProducts(UUID businessId) {
        return productRepository.countOutOfStockProducts(businessId);
    }

    private Pageable createPageable(int page, int limit, String sortBy, String sortOrder) {
        page = Math.max(1, page) - 1;
        limit = Math.min(Math.max(1, limit), 100);

        String sortField = switch (sortBy != null ? sortBy.toLowerCase() : "createdat") {
            case "name" -> "name";
            case "price" -> "price";
            case "category" -> "category";
            case "quantity" -> "quantity";
            default -> "createdAt";
        };

        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(page, limit, Sort.by(direction, sortField));
    }
}
