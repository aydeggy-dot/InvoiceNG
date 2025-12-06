package com.invoiceng.repository;

import com.invoiceng.entity.Payment;
import com.invoiceng.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByReference(String reference);

    List<Payment> findByInvoiceId(UUID invoiceId);

    List<Payment> findByInvoiceIdAndStatus(UUID invoiceId, PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.invoice.user.id = :userId ORDER BY p.createdAt DESC LIMIT :limit")
    List<Payment> findRecentByUserId(@Param("userId") UUID userId, @Param("limit") int limit);

    boolean existsByReference(String reference);
}
