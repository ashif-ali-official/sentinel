package com.sentinel.sanction.service;

import com.sentinel.sanction.entity.SanctionOutboxEvent;
import com.sentinel.sanction.repository.SanctionOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SanctionOutboxClaimService {
    private final SanctionOutboxRepository repository;
    @Transactional
    public List<SanctionOutboxEvent> claim(int batchSize) {
        Instant now = Instant.now();
        repository.releaseExpired(now.minus(60, ChronoUnit.SECONDS));
        List<SanctionOutboxEvent> claimed = new ArrayList<>();
        for (String id : repository.findPendingIds(now).stream().limit(batchSize).toList()) {
            if (repository.claim(id, now) == 1) repository.findById(id).ifPresent(claimed::add);
        }
        return claimed;
    }
}
