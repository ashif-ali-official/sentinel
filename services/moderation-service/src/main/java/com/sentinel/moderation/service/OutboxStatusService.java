package com.sentinel.moderation.service;

import com.sentinel.moderation.entity.ModerationOutboxEvent;
import com.sentinel.moderation.repository.ModerationOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxStatusService {
    private final ModerationOutboxRepository repository;

    @Transactional
    public void markPublished(String id) {
        ModerationOutboxEvent event = repository.findById(id).orElseThrow();
        event.markPublished();
    }

    @Transactional
    public void markFailed(String id, String error, int maxAttempts) {
        ModerationOutboxEvent event = repository.findById(id).orElseThrow();
        event.markFailed(error, maxAttempts);
    }
}
