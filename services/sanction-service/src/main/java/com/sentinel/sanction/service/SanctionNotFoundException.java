package com.sentinel.sanction.service;
public class SanctionNotFoundException extends RuntimeException {
    public SanctionNotFoundException(String caseId) { super("Sanction review not found for case: " + caseId); }
}
