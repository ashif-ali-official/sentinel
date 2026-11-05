package com.sentinel.appeals.service;
import com.sentinel.appeals.entity.AppealOutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;
@Component @RequiredArgsConstructor @Slf4j
public class AppealOutboxPublisher {
    private final AppealOutboxClaimService claims; private final AppealOutboxStatusService statuses; private final KafkaTemplate<String,String> kafka;
    @Value("${sentinel.outbox.batch-size:20}") private int batch;
    @Value("${sentinel.outbox.max-attempts:5}") private int maxAttempts;
    @Scheduled(fixedDelayString="${sentinel.outbox.publish-delay:2000}")
    public void publish(){for(AppealOutboxEvent e:claims.claim(batch)){try{kafka.send(e.getTopic(),e.getAggregateId(),e.getPayload()).get(10,TimeUnit.SECONDS);statuses.published(e.getId());}catch(Exception ex){log.warn("Failed to publish appeal outbox event {}",e.getId(),ex);statuses.failed(e.getId(),ex.getMessage(),maxAttempts);}}}
}
