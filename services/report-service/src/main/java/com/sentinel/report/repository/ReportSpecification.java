package com.sentinel.report.repository;

import com.sentinel.report.entity.Report;
import com.sentinel.report.enums.ReportCategory;
import com.sentinel.report.enums.ReportStatus;
import org.springframework.data.jpa.domain.Specification;

public class ReportSpecification {

    private ReportSpecification() {
    }

    public static Specification<Report> hasStatus(ReportStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Report> hasCategory(ReportCategory category) {
        return (root, query, criteriaBuilder) ->
                category == null ? null :
                        criteriaBuilder.equal(root.get("category"), category);
    }

    public static Specification<Report> reportedPlayerIs(String playerId) {
        return (root, query, criteriaBuilder) ->
                playerId == null ? null :
                        criteriaBuilder.equal(
                                root.get("reportedPlayerId"), playerId
                        );
    }

}
