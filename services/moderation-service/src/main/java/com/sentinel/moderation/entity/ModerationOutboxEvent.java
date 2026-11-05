package com.sentinel.moderation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "moderation_outbox_events")
public class ModerationOutboxEvent {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "aggregate_id", nullable = false, length = 36)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 150)
    private String eventType;

    @Column(nullable = false, length = 200)
    private String topic;

    @Column(nullable = false, columnDefinition = "json")
    private String payload;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "claimed_at")
    private Instant claimedAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "last_error", length = 2000)
    private String lastError;

    protected ModerationOutboxEvent() {}

    public ModerationOutboxEvent(String id, String aggregateId, String eventType, String topic, String payload) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.topic = topic;
        this.payload = payload;
        this.status = "PENDING";
        this.attempts = 0;
        this.createdAt = Instant.now();
        this.nextAttemptAt = this.createdAt;
    }

    public String getId() { return id; }
    public String getAggregateId() { return aggregateId; }
    public String getTopic() { return topic; }
    public String getPayload() { return payload; }
    public String getStatus() { return status; }
    public int getAttempts() { return attempts; }
    public Instant getClaimedAt() { return claimedAt; }

    public void claim() { status = "PROCESSING"; claimedAt = Instant.now(); }
    public void markPublished() { status = "PUBLISHED"; publishedAt = Instant.now(); claimedAt = null; lastError = null; }
    public void markFailed(String error, int maxAttempts) {
        attempts++;
        lastError = error == null ? "Unknown publishing error" : error.substring(0, Math.min(error.length(), 2000));
        claimedAt = null;
        if (attempts >= maxAttempts) {
            status = "FAILED";
        } else {
            status = "PENDING";
            long delaySeconds = Math.min(300L, 1L << Math.min(attempts, 8));
            nextAttemptAt = Instant.now().plusSeconds(delaySeconds);
        }
    }

    public boolean claimExpired(Instant cutoff) { return "PROCESSING".equals(status) && claimedAt != null && claimedAt.isBefore(cutoff); }
}
