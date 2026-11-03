package com.sentinel.cases.service;

import com.sentinel.cases.entity.CaseAuditLog;
import com.sentinel.cases.repository.CaseAuditLogRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaseAuditService {

    private final CaseAuditLogRepository auditLogRepository;

    public void record(
            String caseId,
            String action,
            String oldValue,
            String newValue,
            String performedBy) {

        CaseAuditLog auditLog = new CaseAuditLog(
                caseId,
                action,
                oldValue,
                newValue,
                performedBy
        );

        auditLogRepository.save(auditLog);
    }
}