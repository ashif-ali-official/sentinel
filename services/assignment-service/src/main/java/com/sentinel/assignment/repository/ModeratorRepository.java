package com.sentinel.assignment.repository;

import com.sentinel.assignment.entity.Moderator;
import com.sentinel.assignment.entity.enums.AssignmentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ModeratorRepository
        extends JpaRepository<Moderator, String> {

    boolean existsByUsername(String username);

    Optional<Moderator> findByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT m
    FROM Moderator m
    WHERE m.active = true
      AND m.available = true
      AND m.region = :region
      AND (
          SELECT COUNT(a)
          FROM CaseAssignment a
          WHERE a.moderatorId = m.id
            AND a.status IN :statuses
      ) < m.maxActiveCases
    ORDER BY (
          SELECT COUNT(a)
          FROM CaseAssignment a
          WHERE a.moderatorId = m.id
            AND a.status IN :statuses
      ) ASC, m.createdAt ASC
""")
    List<Moderator> findEligibleModeratorsForUpdate(
            @Param("region") String region,
            @Param("statuses") Collection<AssignmentStatus> statuses
    );

}