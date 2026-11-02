package com.sentinel.report.repository;

import com.sentinel.report.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, String> {
    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(
            String status
    );
}
