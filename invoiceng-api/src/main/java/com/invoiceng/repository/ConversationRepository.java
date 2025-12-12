package com.invoiceng.repository;

import com.invoiceng.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    Optional<Conversation> findByBusinessIdAndCustomerPhone(UUID businessId, String customerPhone);

    Page<Conversation> findByBusinessIdAndIsActiveTrue(UUID businessId, Pageable pageable);

    Page<Conversation> findByBusinessId(UUID businessId, Pageable pageable);

    Page<Conversation> findByBusinessIdAndIsHandedOffTrue(UUID businessId, Pageable pageable);

    List<Conversation> findByBusinessIdAndIsActiveTrueOrderByLastMessageAtDesc(UUID businessId);

    @Query("SELECT c FROM Conversation c WHERE c.business.id = :businessId AND c.isActive = true AND c.lastMessageAt < :cutoff")
    List<Conversation> findStaleConversations(
            @Param("businessId") UUID businessId,
            @Param("cutoff") LocalDateTime cutoff
    );

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.business.id = :businessId AND c.createdAt >= :since")
    long countByBusinessIdSince(@Param("businessId") UUID businessId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.business.id = :businessId AND c.outcome = :outcome AND c.createdAt >= :since")
    long countByBusinessIdAndOutcomeSince(
            @Param("businessId") UUID businessId,
            @Param("outcome") String outcome,
            @Param("since") LocalDateTime since
    );

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.business.id = :businessId AND c.isHandedOff = true AND c.handedOffAt >= :since")
    long countHandedOffSince(@Param("businessId") UUID businessId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.business.id = :businessId")
    long countByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.business.id = :businessId AND c.isActive = true")
    long countActiveByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.business.id = :businessId AND c.outcome = 'converted'")
    long countConvertedByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.business.id = :businessId AND c.outcome = 'abandoned'")
    long countAbandonedByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.business.id = :businessId AND c.isHandedOff = true")
    long countHandedOffByBusinessId(@Param("businessId") UUID businessId);

    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count " +
           "FROM conversations WHERE business_id = :businessId " +
           "AND created_at >= :since GROUP BY DATE(created_at) ORDER BY date", nativeQuery = true)
    List<Object[]> getConversationsByDay(@Param("businessId") UUID businessId, @Param("since") LocalDateTime since);
}
