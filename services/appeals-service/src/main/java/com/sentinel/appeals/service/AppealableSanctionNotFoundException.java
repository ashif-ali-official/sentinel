package com.sentinel.appeals.service;
public class AppealableSanctionNotFoundException extends RuntimeException { public AppealableSanctionNotFoundException(String caseId){super("Applied sanction not found for case: "+caseId);} }
