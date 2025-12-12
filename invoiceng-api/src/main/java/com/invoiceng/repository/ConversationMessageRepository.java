package com.invoiceng.repository;

import com.invoiceng.entity.ConversationMessage;
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
public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, UUID> {

    List<ConversationMessage> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    List<ConversationMessage> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);

    Optional<ConversationMessage> findByWhatsappMessageId(String whatsappMessageId);

    boolean existsByWhatsappMessageId(String whatsappMessageId);

    @Query("SELECT cm FROM ConversationMessage cm WHERE cm.conversation.id = :conversationId ORDER BY cm.createdAt DESC")
    List<ConversationMessage> findRecentMessages(@Param("conversationId") UUID conversationId, Pageable pageable);

    @Query("SELECT COUNT(cm) FROM ConversationMessage cm WHERE cm.conversation.business.id = :businessId AND cm.direction = :direction AND cm.createdAt >= :since")
    long countByBusinessIdAndDirectionSince(
            @Param("businessId") UUID businessId,
            @Param("direction") String direction,
            @Param("since") LocalDateTime since
    );

    @Query("SELECT cm FROM ConversationMessage cm WHERE cm.conversation.id = :conversationId AND cm.direction = 'inbound' ORDER BY cm.createdAt DESC")
    List<ConversationMessage> findLastInboundMessages(@Param("conversationId") UUID conversationId, Pageable pageable);
}
