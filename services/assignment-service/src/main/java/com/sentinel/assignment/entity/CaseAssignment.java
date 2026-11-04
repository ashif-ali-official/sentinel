package com.sentinel.assignment.entity;

import com.sentinel.assignment.entity.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_assignments")
@Getter
@Setter
@NoArgsConstructor
public class CaseAssignment {

    @Id
    private String id;

    @Column(name = "case_id", nullable = false, unique = true)
    private String caseId;

    @Column(name = "moderator_id", nullable = false)
    private String moderatorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssignmentStatus status;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
