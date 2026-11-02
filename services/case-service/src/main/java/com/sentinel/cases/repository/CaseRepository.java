package com.sentinel.cases.repository;

import com.sentinel.cases.entity.ModerationCase;
import com.sentinel.cases.enums.CasePriority;
import com.sentinel.cases.enums.CaseStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CaseRepository extends JpaRepository<ModerationCase, String>, JpaSpecificationExecutor<ModerationCase> {

    boolean existsByReportId(String reportId);
}