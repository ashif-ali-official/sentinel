package com.sentinel.moderation.service;

import com.sentinel.moderation.entity.ModerationOutboxEvent;
import com.sentinel.moderation.repository.ModerationOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxClaimService {
    private final ModerationOutboxRepository repository;

    @Transactional
    public List<ModerationOutboxEvent> claimBatch(int batchSize) {
        Instant now = Instant.now();
        repository.releaseExpiredClaims(now.minus(60, ChronoUnit.SECONDS));
        List<String> candidates = repository.findPendingIds(now);
        List<ModerationOutboxEvent> claimed = new ArrayList<>();
        for (String id : candidates.stream().limit(batchSize).toList()) {
            if (repository.claimPending(id, now) == 1) {
                repository.findById(id).ifPresent(claimed::add);
            }
        }
        return claimed;
    }
}
