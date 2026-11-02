package com.sentinel.cases.exception;

public class CaseNotFoundException extends RuntimeException {

    public CaseNotFoundException(String caseId) {
        super("Moderation case not found: " + caseId);
    }
}