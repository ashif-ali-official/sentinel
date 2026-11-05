package com.sentinel.appeals.service;

import com.sentinel.appeals.dto.event.SanctionAppliedEvent;
import com.sentinel.appeals.entity.AppealableSanction;
import com.sentinel.appeals.entity.ProcessedEvent;
import com.sentinel.appeals.repository.AppealableSanctionRepository;
import com.sentinel.appeals.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class SanctionAppliedEventService {
    private final AppealableSanctionRepository sanctionRepository;
    private final ProcessedEventRepository processedRepository;
    @Transactional
    public void process(SanctionAppliedEvent event){
        if(event==null||event.eventId()==null||event.payload()==null||!"SANCTION_APPLIED".equals(event.eventType())||event.eventVersion()!=1) throw new IllegalArgumentException("Invalid SANCTION_APPLIED event");
        if(processedRepository.existsById(event.eventId()))return;
        SanctionAppliedEvent.Payload p=event.payload();
        if(p.caseId()==null||p.assignmentId()==null||p.playerId()==null||p.sanctionType()==null||p.appliedAt()==null)throw new IllegalArgumentException("SANCTION_APPLIED payload is incomplete");
        if(event.aggregateId()!=null&&!event.aggregateId().equals(p.caseId()))throw new IllegalArgumentException("Event aggregate ID does not match case ID");
        if(!sanctionRepository.existsById(p.caseId()))sanctionRepository.save(new AppealableSanction(p.caseId(),event.eventId(),p.assignmentId(),p.playerId(),p.sanctionType(),p.durationSeconds(),p.appliedAt()));
        processedRepository.save(new ProcessedEvent(event.eventId(),event.eventType(),p.caseId()));
    }
}
