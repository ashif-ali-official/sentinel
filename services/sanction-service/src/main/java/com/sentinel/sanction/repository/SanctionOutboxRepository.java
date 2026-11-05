package com.sentinel.sanction.repository;

import com.sentinel.sanction.entity.SanctionOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;

public interface SanctionOutboxRepository extends JpaRepository<SanctionOutboxEvent, String> {
    @Query("select e.id from SanctionOutboxEvent e where e.status='PENDING' and e.nextAttemptAt <= :now order by e.createdAt")
    List<String> findPendingIds(@Param("now") Instant now);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update SanctionOutboxEvent e set e.status='PROCESSING', e.claimedAt=:now where e.id=:id and e.status='PENDING'")
    int claim(@Param("id") String id, @Param("now") Instant now);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update SanctionOutboxEvent e set e.status='PENDING', e.claimedAt=null where e.status='PROCESSING' and e.claimedAt < :cutoff")
    int releaseExpired(@Param("cutoff") Instant cutoff);
}
