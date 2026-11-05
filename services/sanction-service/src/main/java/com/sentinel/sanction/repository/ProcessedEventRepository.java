package com.sentinel.sanction.repository;

import com.sentinel.sanction.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {}
