package com.sentinel.appeals.controller;

import com.sentinel.appeals.dto.*;
import com.sentinel.appeals.service.AppealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
@RestController
@RequestMapping("/api/v1/appeals")
@RequiredArgsConstructor
public class AppealController {
    private final AppealService appealService;

    @PostMapping("/cases/{caseId}")
    public AppealResponse submit(@PathVariable String caseId, @Valid @RequestBody SubmitAppealRequest request) {
        return appealService.submit(caseId, request);
    }

    @GetMapping("/{appealId}")
    public AppealResponse get(@PathVariable String appealId) {
        return appealService.get(appealId);
    }

    @PatchMapping("/{appealId}/decision")
    public AppealResponse decide(@PathVariable String appealId, @Valid @RequestBody DecideAppealRequest request) {
        return appealService.decide(appealId, request);
    }
}
