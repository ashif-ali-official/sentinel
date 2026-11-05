package com.sentinel.assignment.service;

import com.sentinel.assignment.dto.request.CreateAssignmentRequest;
import com.sentinel.assignment.dto.response.AssignmentResponse;
import com.sentinel.assignment.entity.CaseAssignment;
import com.sentinel.assignment.entity.Moderator;
import com.sentinel.assignment.entity.enums.AssignmentStatus;
import com.sentinel.assignment.repository.CaseAssignmentRepository;
import com.sentinel.assignment.repository.ModeratorRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private static final List<AssignmentStatus> ACTIVE_STATUSES =
            List.of(
                    AssignmentStatus.ASSIGNED,
                    AssignmentStatus.IN_PROGRESS
            );

    private final ModeratorRepository moderatorRepository;
    private final CaseAssignmentRepository assignmentRepository;

    @Transactional
    public AssignmentResponse assignCase(CreateAssignmentRequest request) {

        CaseAssignment saved = createAssignment(request);

        return new AssignmentResponse(
                saved.getId(),
                saved.getCaseId(),
                saved.getModeratorId(),
                saved.getStatus(),
                saved.getAssignedAt()
        );
    }

    @Transactional
    public CaseAssignment createAssignment(CreateAssignmentRequest request) {

        if (assignmentRepository.existsByCaseId(request.caseId())) {
            throw new IllegalStateException(
                    "Case is already assigned: " + request.caseId()
            );
        }

        List<Moderator> moderators =
                moderatorRepository.findEligibleModeratorsForUpdate(
                        request.region(),
                        ACTIVE_STATUSES
                );

        if (moderators.isEmpty()) {
            throw new IllegalStateException(
                    "No eligible moderators available for region: "
                            + request.region()
            );
        }

        Moderator selectedModerator = moderators.get(0);

        CaseAssignment assignment = new CaseAssignment();
        assignment.setId(UUID.randomUUID().toString());
        assignment.setCaseId(request.caseId());
        assignment.setModeratorId(selectedModerator.getId());
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setAssignedAt(LocalDateTime.now());

        return assignmentRepository.save(assignment);
    }

}