package com.sentinel.appeals.service;
import com.sentinel.appeals.entity.AppealOutboxEvent;
import com.sentinel.appeals.repository.AppealOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
@Service @RequiredArgsConstructor
public class AppealOutboxClaimService {
    private final AppealOutboxRepository repository;
    @Transactional public List<AppealOutboxEvent> claim(int size){
        Instant now=Instant.now();repository.releaseExpired(now.minus(60,ChronoUnit.SECONDS));List<AppealOutboxEvent> result=new ArrayList<>();
        for(String id:repository.findPendingIds(now).stream().limit(size).toList())if(repository.claim(id,now)==1)repository.findById(id).ifPresent(result::add);
        return result;
    }
}
