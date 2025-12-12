package com.invoiceng.repository;

import com.invoiceng.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.invoiceCountThisMonth = u.invoiceCountThisMonth + 1 WHERE u.id = :userId")
    void incrementInvoiceCount(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User u SET u.invoiceCountThisMonth = 0, u.invoiceCountResetAt = :resetAt WHERE u.invoiceCountResetAt < :resetAt")
    void resetMonthlyInvoiceCounts(@Param("resetAt") LocalDateTime resetAt);

    Optional<User> findByWhatsappPhoneNumberId(String whatsappPhoneNumberId);

    @Query("SELECT u FROM User u WHERE u.whatsappConnected = true")
    java.util.List<User> findAllWhatsappConnected();
}
