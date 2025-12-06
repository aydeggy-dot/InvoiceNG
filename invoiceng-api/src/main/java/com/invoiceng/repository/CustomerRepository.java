package com.invoiceng.repository;

import com.invoiceng.entity.Customer;
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
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Page<Customer> findByUserId(UUID userId, Pageable pageable);

    Optional<Customer> findByIdAndUserId(UUID id, UUID userId);

    Optional<Customer> findByUserIdAndPhone(UUID userId, String phone);

    boolean existsByUserIdAndPhone(UUID userId, String phone);

    @Query("SELECT c FROM Customer c WHERE c.user.id = :userId AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "c.phone LIKE CONCAT('%', :search, '%'))")
    Page<Customer> searchByUserIdAndNameOrPhone(
            @Param("userId") UUID userId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT c FROM Customer c WHERE c.user.id = :userId ORDER BY c.totalPaid DESC LIMIT :limit")
    List<Customer> findTopCustomersByTotalPaid(@Param("userId") UUID userId, @Param("limit") int limit);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);
}
