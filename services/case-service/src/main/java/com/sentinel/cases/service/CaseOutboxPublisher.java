package com.sentinel.cases.service;

import com.sentinel.cases.entity.CaseOutboxEvent;
import com.sentinel.cases.entity.enums.OutboxEventStatus;
import com.sentinel.cases.repository.CaseOutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseOutboxPublisher {

    private final CaseOutboxEventRepository caseOutboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${sentinel.kafka.topics.case-created}")
    private String caseCreatedTopic;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<CaseOutboxEvent> caseOutboxEventList = caseOutboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);

        for(CaseOutboxEvent event : caseOutboxEventList) {
            try{

                kafkaTemplate.send(
                        event.getTopic(),
                        event.getAggregateId(),
                        event.getPayload()
                ).get();

                event.markPublished();

            } catch (Exception exception) {
                log.error(
                        "Failed to publish outbox event {}",
                        event.getId(),
                        exception
                );

                event.markFailed(exception.getMessage());
            }
        }

        caseOutboxEventRepository.saveAll(caseOutboxEventList);

    }

}
