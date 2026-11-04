package com.sentinel.assignment.service;

import com.sentinel.assignment.client.PlayerClient;
import com.sentinel.assignment.dto.response.PlayerResponse;
import com.sentinel.assignment.kafka.event.CaseCreatedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignmentEventService {

    private final PlayerClient playerClient;
    private final AssignmentEventProcessor eventProcessor;

    public void processCaseCreated(CaseCreatedEvent event) {

        if (event.payload() == null) {
            throw new IllegalArgumentException(
                    "CASE_CREATED event payload is missing"
            );
        }

        PlayerResponse player = playerClient.getPlayer(
                event.payload().reportedPlayerId()
        );

        eventProcessor.process(event, player.region());
    }
}