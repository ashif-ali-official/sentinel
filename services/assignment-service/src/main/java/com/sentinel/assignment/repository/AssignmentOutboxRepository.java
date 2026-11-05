package com.sentinel.assignment.repository;

import com.sentinel.assignment.entity.AssignmentOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentOutboxRepository
        extends JpaRepository<AssignmentOutboxEvent, String> {

    @Query(value = """
        SELECT *
        FROM assignment_outbox_events
        WHERE
            (
                status = 'PENDING'
                AND (
                    next_attempt_at IS NULL
                    OR next_attempt_at <= CURRENT_TIMESTAMP(6)
                )
            )
            OR
            (
                status = 'PROCESSING'
                AND claimed_at <
                    DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 2 MINUTE)
            )
        ORDER BY created_at ASC
        LIMIT :batchSize
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<AssignmentOutboxEvent> findBatchToClaim(
            @Param("batchSize") int batchSize
    );

}