package com.sentinel.assignment.service;

import com.sentinel.assignment.entity.AssignmentOutboxEvent;
import com.sentinel.assignment.repository.AssignmentOutboxRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxStatusService {

    private final AssignmentOutboxRepository outboxRepository;

    @Value("${sentinel.outbox.max-attempts:5}")
    private int maxAttempts;

    @Transactional
    public void markPublished(String id, String claimToken) {

        AssignmentOutboxEvent event = findOwnedEvent(id, claimToken);

        if (event == null) {
            return;
        }

        event.setStatus("PUBLISHED");
        event.setPublishedAt(LocalDateTime.now());
        event.setClaimedBy(null);
        event.setClaimedAt(null);
        event.setLastError(null);

        outboxRepository.save(event);
    }

    @Transactional
    public void markFailed(
            String id,
            String claimToken,
            Exception exception) {

        AssignmentOutboxEvent event = findOwnedEvent(id, claimToken);

        if (event == null) {
            return;
        }

        int attempts = event.getAttempts() + 1;

        event.setAttempts(attempts);
        event.setLastError(exception.getMessage() == null
                ? "Unknown publishing error"
                : exception.getMessage().substring(
                0,
                Math.min(2000, exception.getMessage().length())
        ));

        event.setClaimedBy(null);
        event.setClaimedAt(null);

        if (attempts >= maxAttempts) {
            event.setStatus("FAILED");
        } else {
            event.setStatus("PENDING");

            long delaySeconds = Math.min(
                    60,
                    (long) Math.pow(2, attempts)
            );

            event.setNextAttemptAt(
                    LocalDateTime.now().plusSeconds(delaySeconds)
            );
        }

        outboxRepository.save(event);
    }

    private AssignmentOutboxEvent findOwnedEvent(
            String id,
            String claimToken) {

        AssignmentOutboxEvent event = outboxRepository.findById(id)
                .orElse(null);

        if (event == null
                || !"PROCESSING".equals(event.getStatus())
                || !claimToken.equals(event.getClaimedBy())) {
            return null;
        }

        return event;
    }
}