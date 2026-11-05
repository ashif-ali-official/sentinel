package com.sentinel.appeals.consumer;
import com.sentinel.appeals.dto.event.SanctionAppliedEvent;
import com.sentinel.appeals.service.SanctionAppliedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
@Component @RequiredArgsConstructor @Slf4j
public class SanctionAppliedListener {
    private final ObjectMapper objectMapper; private final SanctionAppliedEventService eventService;
    @KafkaListener(topics="${sentinel.kafka.topics.sanction-applied}")
    public void consume(String message,Acknowledgment acknowledgment){SanctionAppliedEvent e=objectMapper.readValue(message,SanctionAppliedEvent.class);eventService.process(e);acknowledgment.acknowledge();log.info("Registered appealable sanction for case {}",e.aggregateId());}
}
