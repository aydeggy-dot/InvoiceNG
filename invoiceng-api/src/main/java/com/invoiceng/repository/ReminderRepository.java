package com.invoiceng.repository;

import com.invoiceng.entity.Reminder;
import com.invoiceng.entity.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    List<Reminder> findByInvoiceId(UUID invoiceId);

    List<Reminder> findByInvoiceIdAndStatus(UUID invoiceId, ReminderStatus status);

    @Query("SELECT r FROM Reminder r WHERE r.status = 'PENDING' AND r.scheduledAt <= :now")
    List<Reminder> findPendingRemindersToSend(@Param("now") LocalDateTime now);

    @Query("SELECT r FROM Reminder r WHERE r.user.id = :userId AND r.status = 'PENDING'")
    List<Reminder> findPendingByUserId(@Param("userId") UUID userId);

    void deleteByInvoiceId(UUID invoiceId);
}
