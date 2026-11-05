package com.sentinel.sanction.service;

import com.sentinel.sanction.entity.SanctionOutboxEvent;
import com.sentinel.sanction.repository.SanctionOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SanctionOutboxStatusService {
    private final SanctionOutboxRepository repository;
    @Transactional public void published(String id) { SanctionOutboxEvent event=repository.findById(id).orElseThrow(); event.published(); }
    @Transactional public void failed(String id,String error,int maxAttempts) { SanctionOutboxEvent event=repository.findById(id).orElseThrow(); event.failed(error,maxAttempts); }
}
