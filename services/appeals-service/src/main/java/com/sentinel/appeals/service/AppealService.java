package com.sentinel.appeals.service;

import com.sentinel.appeals.dto.AppealResponse;
import com.sentinel.appeals.dto.DecideAppealRequest;
import com.sentinel.appeals.dto.SubmitAppealRequest;
import com.sentinel.appeals.dto.event.AppealDecidedEvent;
import com.sentinel.appeals.entity.Appeal;
import com.sentinel.appeals.entity.AppealOutboxEvent;
import com.sentinel.appeals.entity.AppealableSanction;
import com.sentinel.appeals.repository.AppealOutboxRepository;
import com.sentinel.appeals.repository.AppealRepository;
import com.sentinel.appeals.repository.AppealableSanctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class AppealService {
    private final AppealRepository appealRepository;
    private final AppealableSanctionRepository sanctionRepository;
    private final AppealOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public AppealResponse submit(String caseId,SubmitAppealRequest request){
        AppealableSanction sanction=sanctionRepository.findById(caseId).orElseThrow(()->new AppealableSanctionNotFoundException(caseId));
        if(!sanction.getPlayerId().equals(request.playerId()))throw new IllegalArgumentException("Appeal can only be submitted by the affected player");
        sanction.markAppealed();
        return AppealResponse.from(appealRepository.save(new Appeal(caseId,request.playerId(),request.reason())));
    }
    @Transactional(readOnly=true)
    public AppealResponse get(String appealId){return AppealResponse.from(find(appealId));}
    @Transactional
    public AppealResponse decide(String appealId,DecideAppealRequest request){
        Appeal appeal=find(appealId);
        if(!appeal.getVersion().equals(request.expectedVersion()))throw new ObjectOptimisticLockingFailureException(Appeal.class,appealId);
        AppealableSanction sanction=sanctionRepository.findById(appeal.getCaseId()).orElseThrow(()->new AppealableSanctionNotFoundException(appeal.getCaseId()));
        appeal.decide(request.accepted(),request.decidedBy(),request.decisionNotes());
        sanction.decide(request.accepted());
        Appeal saved=appealRepository.saveAndFlush(appeal);
        if(request.accepted()){
            String eventId=UUID.randomUUID().toString();
            AppealDecidedEvent event=new AppealDecidedEvent(eventId,"APPEAL_DECIDED",1,saved.getCaseId(),Instant.now(),"appeals-service",
                    new AppealDecidedEvent.Payload(saved.getId(),saved.getCaseId(),saved.getPlayerId(),saved.getStatus(),saved.getDecidedBy(),saved.getDecisionNotes(),saved.getDecidedAt()));
            try{outboxRepository.save(new AppealOutboxEvent(eventId,saved.getCaseId(),event.eventType(),"sentinel.appeal.decided",objectMapper.writeValueAsString(event)));}
            catch(Exception exception){throw new IllegalStateException("Failed to serialize appeal decision event",exception);}
        }
        return AppealResponse.from(saved);
    }
    private Appeal find(String id){return appealRepository.findById(id).orElseThrow(()->new AppealNotFoundException(id));}
}
