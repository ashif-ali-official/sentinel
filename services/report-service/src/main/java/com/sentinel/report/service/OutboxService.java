package com.sentinel.report.service;

import com.sentinel.report.entity.OutboxEvent;
import com.sentinel.report.repository.OutboxRepository;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    public void saveEvent(
            String aggregateType,
            String aggregateId,
            String eventType,
            Object event
    ) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = new OutboxEvent(
                    aggregateType,
                    aggregateId,
                    eventType,
                    payload
            );

            outboxRepository.save(outboxEvent);

        } catch (JsonParseException exception) {
            throw new IllegalStateException(
                    "Unable to serialize outbox event", exception
            );
        }
    }
}