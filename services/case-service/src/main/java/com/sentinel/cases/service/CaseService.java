package com.sentinel.cases.service;

import com.sentinel.cases.dto.response.CaseResponse;
import com.sentinel.cases.dto.response.PageResponse;
import com.sentinel.cases.entity.ModerationCase;
import com.sentinel.cases.enums.CasePriority;
import com.sentinel.cases.enums.CaseStatus;
import com.sentinel.cases.exception.CaseNotFoundException;
import com.sentinel.cases.exception.InvalidCaseStatusTransitionException;
import com.sentinel.cases.repository.CaseRepository;
import com.sentinel.cases.repository.CaseSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;

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
            CaseStatus newStatus
    ) {
        ModerationCase moderationCase = findCase(caseId);

        try{

            moderationCase.updateStatus(newStatus);

        } catch ( IllegalStateException e ){
            throw new InvalidCaseStatusTransitionException(e.getMessage());
        }

        return mapToCaseResponse(moderationCase);
    }

    @Transactional
    public CaseResponse updatePriority(
            String caseId,
            CasePriority newPriority
    ) {
        ModerationCase moderationCase = findCase(caseId);
        moderationCase.updatePriority(newPriority);
        return mapToCaseResponse(moderationCase);
    }

    private ModerationCase findCase(String caseId) {
        return caseRepository.findById(caseId).orElseThrow( () -> new CaseNotFoundException(caseId));
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
                entity.getUpdatedAt()
        );
    }

}
