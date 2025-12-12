package com.invoiceng.repository;

import com.invoiceng.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByBusinessId(UUID businessId, Pageable pageable);

    Page<Product> findByBusinessIdAndStatus(UUID businessId, String status, Pageable pageable);

    Page<Product> findByBusinessIdAndCategory(UUID businessId, String category, Pageable pageable);

    List<Product> findByBusinessIdAndStatusOrderByNameAsc(UUID businessId, String status);

    Optional<Product> findByIdAndBusinessId(UUID id, UUID businessId);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.business.id = :businessId AND p.category IS NOT NULL ORDER BY p.category")
    List<String> findCategoriesByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT p FROM Product p WHERE p.business.id = :businessId AND p.status = 'active' " +
           "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchProducts(@Param("businessId") UUID businessId, @Param("query") String query);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.business.id = :businessId AND p.status = 'active'")
    long countActiveProducts(@Param("businessId") UUID businessId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.business.id = :businessId AND p.status = 'active' AND p.trackInventory = true AND p.quantity <= 0")
    long countOutOfStockProducts(@Param("businessId") UUID businessId);
}
