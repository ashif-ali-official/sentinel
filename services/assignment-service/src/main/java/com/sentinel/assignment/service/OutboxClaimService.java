package com.sentinel.assignment.service;

import com.sentinel.assignment.dto.internal.ClaimedOutboxMessage;
import com.sentinel.assignment.entity.AssignmentOutboxEvent;
import com.sentinel.assignment.repository.AssignmentOutboxRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxClaimService {

    private final AssignmentOutboxRepository outboxRepository;

    @Value("${sentinel.outbox.batch-size:20}")
    private int batchSize;

    @Transactional
    public List<ClaimedOutboxMessage> claimBatch() {

        List<AssignmentOutboxEvent> events =
                outboxRepository.findBatchToClaim(batchSize);

        List<ClaimedOutboxMessage> messages = new ArrayList<>();

        for (AssignmentOutboxEvent event : events) {

            String claimToken = UUID.randomUUID().toString();

            event.setStatus("PROCESSING");
            event.setClaimedBy(claimToken);
            event.setClaimedAt(LocalDateTime.now());

            messages.add(new ClaimedOutboxMessage(
                    event.getId(),
                    claimToken,
                    event.getTopic(),
                    event.getAggregateId(),
                    event.getPayload()
            ));
        }

        outboxRepository.saveAll(events);

        return messages;
    }
}