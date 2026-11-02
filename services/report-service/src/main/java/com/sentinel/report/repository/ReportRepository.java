package com.sentinel.report.repository;

import com.sentinel.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReportRepository extends
        JpaRepository<Report, String>,
        JpaSpecificationExecutor<Report> {
}
