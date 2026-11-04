package com.sentinel.cases.repository;

import com.sentinel.cases.entity.CaseOutboxEvent;
import com.sentinel.cases.entity.enums.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseOutboxEventRepository
        extends JpaRepository<CaseOutboxEvent, String> {

    List<CaseOutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(
            OutboxEventStatus status
    );
}
