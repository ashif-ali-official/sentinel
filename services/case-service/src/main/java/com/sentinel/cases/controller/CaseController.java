package com.sentinel.cases.controller;

import com.sentinel.cases.dto.request.UpdateCasePriorityRequest;
import com.sentinel.cases.dto.request.UpdateCaseStatusRequest;
import com.sentinel.cases.dto.response.CaseAuditResponse;
import com.sentinel.cases.dto.response.CaseResponse;
import com.sentinel.cases.dto.response.PageResponse;
import com.sentinel.cases.enums.CasePriority;
import com.sentinel.cases.enums.CaseStatus;
import com.sentinel.cases.service.CaseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
@Validated
public class CaseController {

    private final CaseService caseService;

    @GetMapping("/{caseId}")
    public ResponseEntity<CaseResponse> getCaseById(@PathVariable String caseId) {
        return ResponseEntity.ok(caseService.getCaseById(caseId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CaseResponse>> getCases(
            @RequestParam( required = false) CaseStatus status,
            @RequestParam( required = false) CasePriority priority,
            @RequestParam( required = false) String reportedPlayerId,
            @RequestParam(defaultValue = "0")
            @Min(0) int page,
            @RequestParam(defaultValue = "10")
            @Min(1) @Max(100) int size
    ) {
        return ResponseEntity.ok(
                caseService.getCases(
                        status, priority, reportedPlayerId, page, size
                )
        );
    }

    @PatchMapping("/{caseId}/status")
    public ResponseEntity<CaseResponse> updateStatus(
            @PathVariable String caseId,
            @Valid @RequestBody UpdateCaseStatusRequest request) {

        return ResponseEntity.ok(
                caseService.updateStatus(caseId, request.status(), request.version(), request.performedBy())
        );
    }

    @PatchMapping("/{caseId}/priority")
    public ResponseEntity<CaseResponse> updatePriority(
            @PathVariable String caseId,
            @Valid @RequestBody UpdateCasePriorityRequest request) {

        return ResponseEntity.ok(
                caseService.updatePriority(caseId, request.priority(), request.version(), request.performedBy())
        );
    }

    @GetMapping("/{caseId}/audit")
    public ResponseEntity<List<CaseAuditResponse>> getAuditHistory(
            @PathVariable String caseId) {

        return ResponseEntity.ok(
                caseService.getAuditHistory(caseId)
        );
    }

}
