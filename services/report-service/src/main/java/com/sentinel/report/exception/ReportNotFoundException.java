package com.sentinel.report.exception;

public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException(String reportId) {
        super("Report not found with ID: " + reportId);
    }

}
