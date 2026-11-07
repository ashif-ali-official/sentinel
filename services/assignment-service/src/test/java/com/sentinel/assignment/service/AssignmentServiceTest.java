package com.sentinel.assignment.service;

import com.sentinel.assignment.dto.request.CreateAssignmentRequest;
import com.sentinel.assignment.entity.CaseAssignment;
import com.sentinel.assignment.entity.Moderator;
import com.sentinel.assignment.entity.enums.AssignmentStatus;
import com.sentinel.assignment.repository.CaseAssignmentRepository;
import com.sentinel.assignment.repository.ModeratorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {
    @Mock ModeratorRepository moderatorRepository;
    @Mock CaseAssignmentRepository assignmentRepository;
    @InjectMocks AssignmentService service;

    @Test void assignsToFirstEligibleModerator() {
        Moderator moderator = new Moderator(); moderator.setId("mod-1");
        when(assignmentRepository.existsByCaseId("case-1")).thenReturn(false);
        when(moderatorRepository.findEligibleModeratorsForUpdate(eq("EU"), anyList())).thenReturn(List.of(moderator));
        when(assignmentRepository.save(any(CaseAssignment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.assignCase(new CreateAssignmentRequest("case-1", "EU"));
        assertEquals("case-1", response.caseId());
        assertEquals("mod-1", response.moderatorId());
        assertEquals(AssignmentStatus.ASSIGNED, response.status());
    }

    @Test void rejectsAlreadyAssignedCasesAndEmptyModeratorPool() {
        when(assignmentRepository.existsByCaseId("case-1")).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> service.createAssignment(new CreateAssignmentRequest("case-1", "EU")));
        verifyNoInteractions(moderatorRepository);
        when(assignmentRepository.existsByCaseId("case-2")).thenReturn(false);
        when(moderatorRepository.findEligibleModeratorsForUpdate(eq("EU"), anyList())).thenReturn(List.of());
        assertThrows(IllegalStateException.class, () -> service.createAssignment(new CreateAssignmentRequest("case-2", "EU")));
        verify(assignmentRepository, never()).save(any());
    }
}
