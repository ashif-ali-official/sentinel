package com.sentinel.assignment.controller;

import com.sentinel.assignment.dto.request.*;
import com.sentinel.assignment.dto.response.ModeratorResponse;
import com.sentinel.assignment.service.ModeratorService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
@RestController
@RequestMapping("/api/v1/moderators")
@RequiredArgsConstructor
public class ModeratorController {

    private final ModeratorService moderatorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModeratorResponse createModerator(
            @Valid @RequestBody CreateModeratorRequest request) {

        return moderatorService.createModerator(request);
    }

    @GetMapping("/{id}")
    public ModeratorResponse getModerator(@PathVariable String id) {
        return moderatorService.getModerator(id);
    }

    @GetMapping
    public Page<ModeratorResponse> getModerators(Pageable pageable) {
        return moderatorService.getModerators(pageable);
    }

    @PatchMapping("/{id}/availability")
    public ModeratorResponse updateAvailability(
            @PathVariable String id,
            @Valid @RequestBody UpdateModeratorAvailabilityRequest request) {

        return moderatorService.updateAvailability(id, request);
    }
}