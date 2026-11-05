package com.sentinel.moderation.controller;

import com.sentinel.moderation.dto.ModerationWorkItemResponse;
import com.sentinel.moderation.dto.RecordDecisionRequest;
import com.sentinel.moderation.service.ModerationWorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/moderation/cases")
@RequiredArgsConstructor
public class ModerationWorkController {
    private final ModerationWorkService moderationWorkService;

    @GetMapping
    public List<ModerationWorkItemResponse> getQueue(@RequestParam String moderatorId) {
        return moderationWorkService.getQueue(moderatorId);
    }

    @PatchMapping("/{caseId}/start")
    public ModerationWorkItemResponse start(
            @PathVariable String caseId,
            @RequestParam String moderatorId) {
        return moderationWorkService.start(caseId, moderatorId);
    }

    @PatchMapping("/{caseId}/decision")
    public ModerationWorkItemResponse recordDecision(
            @PathVariable String caseId,
            @Valid @org.springframework.web.bind.annotation.RequestBody RecordDecisionRequest request) {
        return moderationWorkService.recordDecision(caseId, request);
    }
}
