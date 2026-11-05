package com.sentinel.moderation.service;

public class ModerationWorkItemNotFoundException extends RuntimeException {
    public ModerationWorkItemNotFoundException(String caseId) {
        super("Moderation work item not found for case: " + caseId);
    }
}
