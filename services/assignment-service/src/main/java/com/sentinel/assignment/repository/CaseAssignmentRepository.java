package com.sentinel.assignment.repository;

import com.sentinel.assignment.entity.CaseAssignment;
import com.sentinel.assignment.entity.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface CaseAssignmentRepository
        extends JpaRepository<CaseAssignment, String> {

    long countByModeratorIdAndStatusIn(
            String moderatorId,
            Collection<AssignmentStatus> statuses
    );

    boolean existsByCaseId(String caseId);
}