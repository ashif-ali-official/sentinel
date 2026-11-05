package com.sentinel.sanction.service;

import com.sentinel.sanction.entity.SanctionOutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class SanctionOutboxPublisher {
    private final SanctionOutboxClaimService claimService;
    private final SanctionOutboxStatusService statusService;
    private final KafkaTemplate<String,String> kafkaTemplate;
    @Value("${sentinel.outbox.batch-size:20}") private int batchSize;
    @Value("${sentinel.outbox.max-attempts:5}") private int maxAttempts;
    @Scheduled(fixedDelayString="${sentinel.outbox.publish-delay:2000}")
    public void publishPending() {
        for (SanctionOutboxEvent event : claimService.claim(batchSize)) {
            try {
                kafkaTemplate.send(event.getTopic(),event.getAggregateId(),event.getPayload()).get(10, TimeUnit.SECONDS);
                statusService.published(event.getId());
            } catch (Exception exception) {
                log.warn("Failed to publish sanction outbox event {}",event.getId(),exception);
                statusService.failed(event.getId(),exception.getMessage(),maxAttempts);
            }
        }
    }
}
