package com.sentinel.report.service;

import com.sentinel.report.entity.OutboxEvent;
import com.sentinel.report.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final String TOPIC = "sentinel.report.created";

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 2000)
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxRepository.findTop50ByStatusOrderByCreatedAtAsc("PENDING");

        for (OutboxEvent event : events) {
            kafkaTemplate.send(
                    TOPIC,
                    event.getAggregateId(),
                    event.getPayload()
            ).whenComplete((result, exception) -> {
                if (exception == null) {
                    markPublished(event);
                } else {
                    System.err.println(
                            "Failed to publish event: " + event.getId()
                    );
                }
            });
        }

    }

    private void markPublished(OutboxEvent event) {
        event.markPublished();
        outboxRepository.save(event);
    }

}
