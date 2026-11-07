package com.sentinel.appeals.service;

import com.sentinel.appeals.dto.SubmitAppealRequest;
import com.sentinel.appeals.repository.AppealOutboxRepository;
import com.sentinel.appeals.repository.AppealRepository;
import com.sentinel.appeals.repository.AppealableSanctionRepository;
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
class AppealServiceTest {
    @Mock AppealRepository appeals;
    @Mock AppealableSanctionRepository sanctions;
    @Mock AppealOutboxRepository outbox;
    @Mock ObjectMapper objectMapper;
    @InjectMocks AppealService service;

    @Test void cannotSubmitAgainstUnknownSanction() {
        when(sanctions.findById("case-1")).thenReturn(Optional.empty());
        assertThrows(AppealableSanctionNotFoundException.class,
                () -> service.submit("case-1", new SubmitAppealRequest("player-1", "reason")));
        verifyNoInteractions(appeals, outbox);
    }

    @Test void unknownAppealCannotBeRead() {
        when(appeals.findById("appeal-1")).thenReturn(Optional.empty());
        assertThrows(AppealNotFoundException.class, () -> service.get("appeal-1"));
    }
}
