package com.sentinel.cases.consumer;

import com.sentinel.cases.dto.event.ReportCreatedEvent;
import com.sentinel.cases.service.CaseEventConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportCreatedEventListener {

    private final ObjectMapper objectMapper;
    private final CaseEventConsumerService caseEventConsumerService;

    @KafkaListener(
            topics = "${sentinel.kafka.topics.report-created}"
    )
    public void consume(
            String message,
            Acknowledgment acknowledgment
    ) {
        try{

            log.info("Received ReportCreated event: {}", message);
            ReportCreatedEvent reportCreatedEvent = objectMapper.readValue(message, ReportCreatedEvent.class);
            caseEventConsumerService.processReportCreated(reportCreatedEvent);
            acknowledgment.acknowledge();
            log.info("Successfully processed ReportCreated event: {}", reportCreatedEvent.eventId());

        } catch (JsonParseException e) {
            throw new IllegalStateException(
                    "Failed to deserialize ReportCreated event",
                    e
            );
        }
    }

}
