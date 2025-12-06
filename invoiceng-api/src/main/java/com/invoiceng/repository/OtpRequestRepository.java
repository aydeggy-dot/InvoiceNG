package com.invoiceng.repository;

import com.invoiceng.entity.OtpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRequestRepository extends JpaRepository<OtpRequest, UUID> {

    @Query("SELECT o FROM OtpRequest o WHERE o.phone = :phone AND o.expiresAt > :now AND o.verified = false ORDER BY o.createdAt DESC LIMIT 1")
    Optional<OtpRequest> findLatestValidOtp(@Param("phone") String phone, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(o) FROM OtpRequest o WHERE o.phone = :phone AND o.createdAt > :since")
    long countRecentRequests(@Param("phone") String phone, @Param("since") LocalDateTime since);

    @Modifying
    @Query("DELETE FROM OtpRequest o WHERE o.expiresAt < :now")
    void deleteExpiredOtps(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE OtpRequest o SET o.verified = true WHERE o.id = :id")
    void markAsVerified(@Param("id") UUID id);
}
