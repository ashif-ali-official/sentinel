package com.sentinel.cases.service;

import com.sentinel.cases.dto.response.CaseAuditResponse;
import com.sentinel.cases.dto.response.CaseResponse;
import com.sentinel.cases.dto.response.PageResponse;
import com.sentinel.cases.entity.ModerationCase;
import com.sentinel.cases.enums.CasePriority;
import com.sentinel.cases.enums.CaseStatus;
import com.sentinel.cases.exception.CaseNotFoundException;
import com.sentinel.cases.exception.InvalidCaseStatusTransitionException;
import com.sentinel.cases.repository.CaseAuditLogRepository;
import com.sentinel.cases.repository.CaseRepository;
import com.sentinel.cases.repository.CaseSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseAuditLogRepository caseAuditLogRepository;
    private final CaseAuditService caseAuditService;

    @Transactional(readOnly = true)
    public CaseResponse getCaseById(String caseId) {
        ModerationCase moderationCase = findCase(caseId);
        return mapToCaseResponse(moderationCase);
    }

    public PageResponse<CaseResponse> getCases(
            CaseStatus status,
            CasePriority priority,
            String reportedPlayerId,
            int page,
            int size
    ) {
        Specification<ModerationCase> specification = Specification.allOf(
                List.of(
                        CaseSpecification.hasStatus(status),
                        CaseSpecification.hasPriority(priority),
                        CaseSpecification.reportedPlayerIs(reportedPlayerId)
                )
        );

        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        Page<ModerationCase> casePage = caseRepository.findAll(specification, pageRequest);

        return new PageResponse<>(
                casePage.getContent()
                        .stream()
                        .map(this::mapToCaseResponse)
                        .toList(),
                casePage.getNumber(),
                casePage.getSize(),
                casePage.getTotalElements(),
                casePage.getTotalPages(),
                casePage.isLast()
        );

    }

    @Transactional
    public CaseResponse updateStatus(
            String caseId,
            CaseStatus newStatus,
            Long expectedVersion,
            String performedBy
    ) {
        ModerationCase moderationCase = findCase(caseId);
        validateVersion(moderationCase, expectedVersion);
        CaseStatus oldStatus = moderationCase.getStatus();

        try{

            moderationCase.updateStatus(newStatus);

        } catch ( IllegalStateException e ){
            throw new InvalidCaseStatusTransitionException(e.getMessage());
        }

        caseAuditService.record(
                caseId,
                "STATUS_CHANGED",
                oldStatus.name(),
                newStatus.name(),
                performedBy
        );

        return mapToCaseResponse(moderationCase);
    }

    @Transactional
    public CaseResponse updatePriority(
            String caseId,
            CasePriority newPriority,
            Long expectedVersion,
            String performedBy
    ) {
        ModerationCase moderationCase = findCase(caseId);
        validateVersion(moderationCase, expectedVersion);

        CasePriority oldPriority = moderationCase.getPriority();
        moderationCase.updatePriority(newPriority);

        caseAuditService.record(
                caseId,
                "STATUS_CHANGED",
                oldPriority.name(),
                newPriority.name(),
                performedBy
        );

        return mapToCaseResponse(moderationCase);
    }

    @Transactional(readOnly = true)
    public List<CaseAuditResponse> getAuditHistory(String caseId) {

        findCase(caseId);

        return caseAuditLogRepository
                .findByCaseIdOrderByCreatedAtDesc(caseId)
                .stream()
                .map(audit -> new CaseAuditResponse(
                        audit.getId(),
                        audit.getCaseId(),
                        audit.getAction(),
                        audit.getOldValue(),
                        audit.getNewValue(),
                        audit.getPerformedBy(),
                        audit.getCreatedAt()
                ))
                .toList();
    }

    private ModerationCase findCase(String caseId) {
        return caseRepository.findById(caseId).orElseThrow( () -> new CaseNotFoundException(caseId));
    }

    private void validateVersion(
            ModerationCase moderationCase,
            Long expectedVersion) {

        if (!moderationCase.getVersion().equals(expectedVersion)) {
            throw new ObjectOptimisticLockingFailureException(
                    ModerationCase.class,
                    moderationCase.getId()
            );
        }
    }

    private CaseResponse mapToCaseResponse(ModerationCase entity) {
        return new CaseResponse(
                entity.getId(),
                entity.getReportId(),
                entity.getReporterId(),
                entity.getReportedPlayerId(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

}
