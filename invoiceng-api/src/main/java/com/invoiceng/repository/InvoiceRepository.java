package com.invoiceng.repository;

import com.invoiceng.entity.Invoice;
import com.invoiceng.entity.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByIdAndUserId(UUID id, UUID userId);

    Optional<Invoice> findByPaymentRef(String paymentRef);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Page<Invoice> findByUserId(UUID userId, Pageable pageable);

    Page<Invoice> findByUserIdAndStatus(UUID userId, InvoiceStatus status, Pageable pageable);

    Page<Invoice> findByUserIdAndCustomerId(UUID userId, UUID customerId, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId AND " +
            "(i.invoiceNumber LIKE CONCAT('%', :search, '%') OR " +
            "i.customer.name LIKE CONCAT('%', :search, '%'))")
    Page<Invoice> searchByUserIdAndInvoiceNumberOrCustomerName(
            @Param("userId") UUID userId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId AND i.status = :status AND " +
            "i.createdAt BETWEEN :fromDate AND :toDate")
    Page<Invoice> findByUserIdAndStatusAndDateRange(
            @Param("userId") UUID userId,
            @Param("status") InvoiceStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    // Find overdue invoices for status update job
    @Query("SELECT i FROM Invoice i WHERE i.status IN ('SENT', 'VIEWED') AND i.dueDate < :today")
    List<Invoice> findOverdueInvoices(@Param("today") LocalDate today);

    // Dashboard statistics queries
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.user.id = :userId AND " +
            "i.createdAt >= :startDate")
    long countInvoicesByUserIdSince(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.user.id = :userId AND " +
            "i.status = :status AND i.createdAt >= :startDate")
    long countByUserIdAndStatusSince(
            @Param("userId") UUID userId,
            @Param("status") InvoiceStatus status,
            @Param("startDate") LocalDateTime startDate
    );

    @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i WHERE i.user.id = :userId AND " +
            "i.status = 'PAID' AND i.paidAt >= :startDate")
    BigDecimal sumPaidAmountByUserIdSince(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i WHERE i.user.id = :userId AND " +
            "i.status IN ('SENT', 'VIEWED') AND i.createdAt >= :startDate")
    BigDecimal sumPendingAmountByUserIdSince(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i WHERE i.user.id = :userId AND " +
            "i.status = 'OVERDUE' AND i.createdAt >= :startDate")
    BigDecimal sumOverdueAmountByUserIdSince(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate);

    // Invoice number generation - globally unique (not per-user)
    @Query("SELECT MAX(CAST(SUBSTRING(i.invoiceNumber, 12, 5) AS int)) FROM Invoice i " +
            "WHERE i.invoiceNumber LIKE :prefix")
    Optional<Integer> findMaxInvoiceSequence(@Param("prefix") String prefix);

    // Recent invoices
    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId ORDER BY i.createdAt DESC LIMIT :limit")
    List<Invoice> findRecentByUserId(@Param("userId") UUID userId, @Param("limit") int limit);

    // Update invoice status
    @Modifying
    @Query("UPDATE Invoice i SET i.status = :status WHERE i.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") InvoiceStatus status);

    // Count for subscription limits
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.user.id = :userId AND " +
            "i.createdAt >= :startOfMonth")
    long countInvoicesThisMonth(@Param("userId") UUID userId, @Param("startOfMonth") LocalDateTime startOfMonth);
}
