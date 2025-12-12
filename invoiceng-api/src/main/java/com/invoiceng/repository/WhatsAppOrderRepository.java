package com.invoiceng.repository;

import com.invoiceng.entity.WhatsAppOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WhatsAppOrderRepository extends JpaRepository<WhatsAppOrder, UUID> {

    Page<WhatsAppOrder> findByBusinessId(UUID businessId, Pageable pageable);

    Page<WhatsAppOrder> findByBusinessIdAndPaymentStatus(UUID businessId, String paymentStatus, Pageable pageable);

    Page<WhatsAppOrder> findByBusinessIdAndFulfillmentStatus(UUID businessId, String fulfillmentStatus, Pageable pageable);

    Optional<WhatsAppOrder> findByIdAndBusinessId(UUID id, UUID businessId);

    Optional<WhatsAppOrder> findByOrderNumber(String orderNumber);

    Optional<WhatsAppOrder> findByPaymentReference(String paymentReference);

    List<WhatsAppOrder> findByCustomerPhone(String customerPhone);

    @Query("SELECT COUNT(o) FROM WhatsAppOrder o WHERE o.business.id = :businessId AND o.createdAt >= :since")
    long countOrdersSince(@Param("businessId") UUID businessId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(o) FROM WhatsAppOrder o WHERE o.business.id = :businessId AND o.paymentStatus = 'paid' AND o.paidAt >= :since")
    long countPaidOrdersSince(@Param("businessId") UUID businessId, @Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM WhatsAppOrder o WHERE o.business.id = :businessId AND o.paymentStatus = 'paid' AND o.paidAt >= :since")
    BigDecimal sumRevenueSince(@Param("businessId") UUID businessId, @Param("since") LocalDateTime since);

    @Query("SELECT o FROM WhatsAppOrder o WHERE o.business.id = :businessId AND o.paymentStatus = 'pending' AND o.createdAt < :cutoff")
    List<WhatsAppOrder> findStaleUnpaidOrders(@Param("businessId") UUID businessId, @Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT COUNT(o) FROM WhatsAppOrder o WHERE o.business.id = :businessId")
    long countByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT COUNT(o) FROM WhatsAppOrder o WHERE o.business.id = :businessId AND o.paymentStatus = :status")
    long countByBusinessIdAndPaymentStatus(@Param("businessId") UUID businessId, @Param("status") String status);

    @Query("SELECT COUNT(o) FROM WhatsAppOrder o WHERE o.business.id = :businessId AND o.fulfillmentStatus = :status")
    long countByBusinessIdAndFulfillmentStatus(@Param("businessId") UUID businessId, @Param("status") String status);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM WhatsAppOrder o WHERE o.business.id = :businessId AND o.paymentStatus = 'paid'")
    BigDecimal sumTotalRevenue(@Param("businessId") UUID businessId);

    @Query("SELECT COALESCE(AVG(o.total), 0) FROM WhatsAppOrder o WHERE o.business.id = :businessId AND o.paymentStatus = 'paid'")
    BigDecimal avgOrderValue(@Param("businessId") UUID businessId);

    @Query(value = "SELECT DATE(paid_at) as date, SUM(total) as revenue, COUNT(*) as count " +
           "FROM whatsapp_orders WHERE business_id = :businessId AND payment_status = 'paid' " +
           "AND paid_at >= :since GROUP BY DATE(paid_at) ORDER BY date", nativeQuery = true)
    List<Object[]> getRevenueByDay(@Param("businessId") UUID businessId, @Param("since") LocalDateTime since);

    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count " +
           "FROM whatsapp_orders WHERE business_id = :businessId " +
           "AND created_at >= :since GROUP BY DATE(created_at) ORDER BY date", nativeQuery = true)
    List<Object[]> getOrdersByDay(@Param("businessId") UUID businessId, @Param("since") LocalDateTime since);
}
