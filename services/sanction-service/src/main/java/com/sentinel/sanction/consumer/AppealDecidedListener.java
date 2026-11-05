package com.sentinel.sanction.consumer;
import com.sentinel.sanction.dto.event.AppealDecidedEvent;
import com.sentinel.sanction.service.AppealDecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
@Component @RequiredArgsConstructor
public class AppealDecidedListener {
    private final ObjectMapper objectMapper;private final AppealDecisionService decisionService;
    @KafkaListener(topics="${sentinel.kafka.topics.appeal-decided}")
    public void consume(String message,Acknowledgment acknowledgment){AppealDecidedEvent event=objectMapper.readValue(message,AppealDecidedEvent.class);decisionService.process(event);acknowledgment.acknowledge();}
}
