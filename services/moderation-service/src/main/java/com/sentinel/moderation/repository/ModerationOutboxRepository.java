package com.sentinel.moderation.repository;

import com.sentinel.moderation.entity.ModerationOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ModerationOutboxRepository extends JpaRepository<ModerationOutboxEvent, String> {
    @Query("select e.id from ModerationOutboxEvent e where e.status = 'PENDING' and e.nextAttemptAt <= :now order by e.createdAt asc")
    List<String> findPendingIds(@Param("now") Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ModerationOutboxEvent e set e.status = 'PROCESSING', e.claimedAt = :claimedAt where e.id = :id and e.status = 'PENDING'")
    int claimPending(@Param("id") String id, @Param("claimedAt") Instant claimedAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ModerationOutboxEvent e set e.status = 'PENDING', e.claimedAt = null where e.status = 'PROCESSING' and e.claimedAt < :cutoff")
    int releaseExpiredClaims(@Param("cutoff") Instant cutoff);
}
