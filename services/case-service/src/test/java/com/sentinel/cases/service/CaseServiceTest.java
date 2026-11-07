package com.sentinel.cases.service;

import com.sentinel.cases.repository.CaseAuditLogRepository;
import com.sentinel.cases.repository.CaseRepository;
import com.sentinel.cases.exception.CaseNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {
    @Mock CaseRepository cases;
    @Mock CaseAuditLogRepository auditLogs;
    @Mock CaseAuditService auditService;
    @InjectMocks CaseService service;

    @Test void unknownCaseCannotBeReadOrAudited() {
        when(cases.findById("case-1")).thenReturn(Optional.empty());
        assertThrows(CaseNotFoundException.class, () -> service.getCaseById("case-1"));
        assertThrows(CaseNotFoundException.class, () -> service.getAuditHistory("case-1"));
        verifyNoInteractions(auditLogs, auditService);
    }
}
