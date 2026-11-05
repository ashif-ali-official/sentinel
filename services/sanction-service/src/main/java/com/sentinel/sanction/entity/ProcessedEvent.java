package com.sentinel.sanction.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {
    @Id @Column(name = "event_id", length = 36) private String eventId;
    @Column(name = "event_type", nullable = false, length = 150) private String eventType;
    @Column(name = "aggregate_id", nullable = false, length = 36) private String aggregateId;
    @Column(name = "processed_at", nullable = false) private Instant processedAt;
    protected ProcessedEvent() {}
    public ProcessedEvent(String eventId, String eventType, String aggregateId) {
        this.eventId = eventId; this.eventType = eventType; this.aggregateId = aggregateId; this.processedAt = Instant.now();
    }
}
