package com.sentinel.assignment.service;

import com.sentinel.assignment.dto.request.*;
import com.sentinel.assignment.dto.response.ModeratorResponse;
import com.sentinel.assignment.entity.Moderator;
import com.sentinel.assignment.entity.enums.AssignmentStatus;
import com.sentinel.assignment.exception.*;
import com.sentinel.assignment.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModeratorService {

    private static final List<AssignmentStatus> ACTIVE_STATUSES =
            List.of(
                    AssignmentStatus.ASSIGNED,
                    AssignmentStatus.IN_PROGRESS
            );

    private final ModeratorRepository moderatorRepository;
    private final CaseAssignmentRepository assignmentRepository;

    @Transactional
    public ModeratorResponse createModerator(
            CreateModeratorRequest request) {

        if (moderatorRepository.existsByUsername(request.username())) {
            throw new ModeratorAlreadyExistsException(request.username());
        }

        Moderator moderator = new Moderator();
        moderator.setId(UUID.randomUUID().toString());
        moderator.setUsername(request.username());
        moderator.setDisplayName(request.displayName());
        moderator.setRegion(request.region());
        moderator.setMaxActiveCases(request.maxActiveCases());

        Moderator saved = moderatorRepository.save(moderator);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ModeratorResponse getModerator(String id) {

        Moderator moderator = findModerator(id);

        return toResponse(moderator);
    }

    @Transactional(readOnly = true)
    public Page<ModeratorResponse> getModerators(Pageable pageable) {

        return moderatorRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ModeratorResponse updateAvailability(
            String id,
            UpdateModeratorAvailabilityRequest request) {

        Moderator moderator = findModerator(id);

        moderator.setAvailable(request.available());

        return toResponse(moderatorRepository.save(moderator));
    }

    private Moderator findModerator(String id) {
        return moderatorRepository.findById(id)
                .orElseThrow(() -> new ModeratorNotFoundException(id));
    }

    private ModeratorResponse toResponse(Moderator moderator) {

        long activeCases =
                assignmentRepository.countByModeratorIdAndStatusIn(
                        moderator.getId(),
                        ACTIVE_STATUSES
                );

        return new ModeratorResponse(
                moderator.getId(),
                moderator.getUsername(),
                moderator.getDisplayName(),
                moderator.getRegion(),
                moderator.isActive(),
                moderator.isAvailable(),
                moderator.getMaxActiveCases(),
                activeCases,
                moderator.getCreatedAt()
        );
    }
}