package com.sentinel.assignment.kafka.producer;

import com.sentinel.assignment.dto.internal.ClaimedOutboxMessage;
import com.sentinel.assignment.service.OutboxClaimService;
import com.sentinel.assignment.service.OutboxStatusService;

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
public class AssignmentOutboxPublisher {

    private final OutboxClaimService claimService;
    private final OutboxStatusService statusService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString =
            "${sentinel.outbox.publish-delay:2000}")
    public void publishPendingEvents() {

        List<ClaimedOutboxMessage> messages =
                claimService.claimBatch();

        for (ClaimedOutboxMessage message : messages) {

            try {
                kafkaTemplate.send(
                        message.topic(),
                        message.aggregateId(),
                        message.payload()
                ).get(10, TimeUnit.SECONDS);

                statusService.markPublished(
                        message.id(),
                        message.claimToken()
                );

                log.info(
                        "Outbox event published. id={}, topic={}",
                        message.id(),
                        message.topic()
                );

            } catch (Exception ex) {

                log.error(
                        "Outbox publishing failed. id={}",
                        message.id(),
                        ex
                );

                statusService.markFailed(
                        message.id(),
                        message.claimToken(),
                        ex
                );
            }
        }
    }
}