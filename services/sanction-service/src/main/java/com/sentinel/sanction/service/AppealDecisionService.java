package com.sentinel.sanction.service;

import com.sentinel.sanction.dto.event.AppealDecidedEvent;
import com.sentinel.sanction.dto.event.SanctionReversedEvent;
import com.sentinel.sanction.entity.ProcessedEvent;
import com.sentinel.sanction.entity.SanctionOutboxEvent;
import com.sentinel.sanction.entity.SanctionReview;
import com.sentinel.sanction.repository.ProcessedEventRepository;
import com.sentinel.sanction.repository.SanctionOutboxRepository;
import com.sentinel.sanction.repository.SanctionReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class AppealDecisionService {
    private final ProcessedEventRepository processedEvents;
    private final SanctionReviewRepository reviewRepository;
    private final SanctionOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void process(AppealDecidedEvent event){
        if(event==null||event.eventId()==null||event.payload()==null||!"APPEAL_DECIDED".equals(event.eventType())||event.eventVersion()!=1)throw new IllegalArgumentException("Invalid APPEAL_DECIDED event");
        if(processedEvents.existsById(event.eventId()))return;
        AppealDecidedEvent.Payload p=event.payload();
        if(p.caseId()==null||p.decision()==null||p.decidedAt()==null)throw new IllegalArgumentException("Appeal decision payload is incomplete");
        if(!"ACCEPTED".equals(p.decision())){processedEvents.save(new ProcessedEvent(event.eventId(),event.eventType(),p.caseId()));return;}
        SanctionReview review=reviewRepository.findById(p.caseId()).orElseThrow(()->new IllegalArgumentException("Sanction review not found for case "+p.caseId()));
        review.reverse("Appeal "+p.appealId()+" accepted: "+p.decisionNotes());
        reviewRepository.saveAndFlush(review);
        String eventId=UUID.randomUUID().toString();
        SanctionReversedEvent reversed=new SanctionReversedEvent(eventId,"SANCTION_REVERSED",1,p.caseId(),Instant.now(),"sanction-service",
                new SanctionReversedEvent.Payload(p.caseId(),review.getReportedPlayerId(),review.getSanctionType(),review.getReversalReason(),p.decidedBy(),review.getReversedAt()));
        try{outboxRepository.save(new SanctionOutboxEvent(eventId,p.caseId(),reversed.eventType(),"sentinel.sanction.reversed",objectMapper.writeValueAsString(reversed)));}
        catch(Exception ex){throw new IllegalStateException("Failed to serialize sanction reversal event",ex);}
        processedEvents.save(new ProcessedEvent(event.eventId(),event.eventType(),p.caseId()));
    }
}
