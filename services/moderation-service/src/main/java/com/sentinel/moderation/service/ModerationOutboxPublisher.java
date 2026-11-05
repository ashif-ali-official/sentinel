package com.sentinel.moderation.service;

import com.sentinel.moderation.entity.ModerationOutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationOutboxPublisher {
    private final OutboxClaimService claimService;
    private final OutboxStatusService statusService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${sentinel.outbox.batch-size:20}")
    private int batchSize;

    @Value("${sentinel.outbox.max-attempts:5}")
    private int maxAttempts;

    @Scheduled(fixedDelayString = "${sentinel.outbox.publish-delay:2000}")
    public void publishPending() {
        List<ModerationOutboxEvent> events = claimService.claimBatch(batchSize);
        for (ModerationOutboxEvent event : events) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload())
                        .get(10, TimeUnit.SECONDS);
                statusService.markPublished(event.getId());
            } catch (Exception exception) {
                log.warn("Failed to publish moderation outbox event {}", event.getId(), exception);
                statusService.markFailed(event.getId(), exception.getMessage(), maxAttempts);
            }
        }
    }
}
