package com.sentinel.moderation.repository;

import com.sentinel.moderation.entity.ModerationWorkItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModerationWorkItemRepository extends JpaRepository<ModerationWorkItem, String> {
    List<ModerationWorkItem> findByModeratorIdOrderByAssignedAtAsc(String moderatorId);
}
