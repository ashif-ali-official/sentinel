package com.sentinel.assignment.dto.response;

import com.sentinel.assignment.entity.enums.AssignmentStatus;
import java.time.LocalDateTime;

public record AssignmentResponse(
        String id,
        String caseId,
        String moderatorId,
        AssignmentStatus status,
        LocalDateTime assignedAt
) {}