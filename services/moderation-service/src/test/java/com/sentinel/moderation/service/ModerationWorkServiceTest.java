package com.sentinel.moderation.service;

import com.sentinel.moderation.repository.ModerationOutboxRepository;
import com.sentinel.moderation.repository.ModerationWorkItemRepository;
import com.sentinel.moderation.repository.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModerationWorkServiceTest {
    @Mock ModerationWorkItemRepository workItems;
    @Mock ProcessedEventRepository processedEvents;
    @Mock ModerationOutboxRepository outbox;
    @Mock ObjectMapper objectMapper;
    @InjectMocks ModerationWorkService service;

    @Test void rejectsMalformedAssignmentEventBeforePersistence() {
        assertThrows(IllegalArgumentException.class, () -> service.processAssignment(null));
        verifyNoInteractions(workItems, processedEvents, outbox);
    }

    @Test void rejectsInvalidQueueIdentity() {
        assertThrows(IllegalArgumentException.class, () -> service.getQueue(" "));
        verifyNoInteractions(workItems);
    }
}
