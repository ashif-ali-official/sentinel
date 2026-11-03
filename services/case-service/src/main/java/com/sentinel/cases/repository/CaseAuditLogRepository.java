package com.sentinel.cases.repository;

import com.sentinel.cases.entity.CaseAuditLog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseAuditLogRepository
        extends JpaRepository<CaseAuditLog, String> {

    List<CaseAuditLog> findByCaseIdOrderByCreatedAtDesc(String caseId);
}