package com.sentinel.sanction.service;

import com.sentinel.sanction.repository.SanctionOutboxRepository;
import com.sentinel.sanction.repository.SanctionReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SanctionServiceTest {
    @Mock SanctionReviewRepository reviews;
    @Mock SanctionOutboxRepository outbox;
    @Mock ObjectMapper objectMapper;
    @InjectMocks SanctionService service;

    @Test void unknownSanctionCannotBeReadOrApplied() {
        when(reviews.findById("case-1")).thenReturn(Optional.empty());
        assertThrows(SanctionNotFoundException.class, () -> service.get("case-1"));
        assertThrows(SanctionNotFoundException.class, () -> service.apply("case-1", null));
        verifyNoInteractions(outbox);
    }
}
