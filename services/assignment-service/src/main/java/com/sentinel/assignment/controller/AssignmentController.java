package com.sentinel.assignment.controller;

import com.sentinel.assignment.dto.request.CreateAssignmentRequest;
import com.sentinel.assignment.dto.response.AssignmentResponse;
import com.sentinel.assignment.service.AssignmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentResponse assignCase(
            @Valid @RequestBody CreateAssignmentRequest request) {

        return assignmentService.assignCase(request);
    }
}