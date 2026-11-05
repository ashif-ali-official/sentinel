package com.sentinel.appeals.service;
import com.sentinel.appeals.entity.AppealOutboxEvent;
import com.sentinel.appeals.repository.AppealOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class AppealOutboxStatusService {
    private final AppealOutboxRepository repository;
    @Transactional public void published(String id){AppealOutboxEvent event=repository.findById(id).orElseThrow();event.published();}
    @Transactional public void failed(String id,String error,int max){AppealOutboxEvent event=repository.findById(id).orElseThrow();event.failed(error,max);}
}
