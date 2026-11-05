package com.sentinel.appeals.repository;
import com.sentinel.appeals.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent,String>{}
