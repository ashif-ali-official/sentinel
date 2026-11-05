package com.sentinel.sanction.controller;

import com.sentinel.sanction.dto.ApplySanctionRequest;
import com.sentinel.sanction.dto.SanctionReviewResponse;
import com.sentinel.sanction.service.SanctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sanctions")
@RequiredArgsConstructor
public class SanctionController {
    private final SanctionService sanctionService;
    @GetMapping("/{caseId}") public SanctionReviewResponse get(@PathVariable String caseId) { return sanctionService.get(caseId); }
    @PostMapping("/{caseId}/apply") public SanctionReviewResponse apply(@PathVariable String caseId, @Valid @RequestBody ApplySanctionRequest request) {
        return sanctionService.apply(caseId, request);
    }
}
