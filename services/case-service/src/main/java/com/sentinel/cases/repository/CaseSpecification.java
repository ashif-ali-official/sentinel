package com.sentinel.cases.repository;

import com.sentinel.cases.entity.ModerationCase;
import com.sentinel.cases.enums.CasePriority;
import com.sentinel.cases.enums.CaseStatus;

import org.springframework.data.jpa.domain.Specification;

public final class CaseSpecification {

    private CaseSpecification() {
    }

    public static Specification<ModerationCase> hasStatus(
            CaseStatus status) {

        return (root, query, criteriaBuilder) ->
                status == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<ModerationCase> hasPriority(
            CasePriority priority) {

        return (root, query, criteriaBuilder) ->
                priority == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<ModerationCase> reportedPlayerIs(
            String playerId) {

        return (root, query, criteriaBuilder) ->
                playerId == null || playerId.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(
                        root.get("reportedPlayerId"), playerId
                );
    }
}