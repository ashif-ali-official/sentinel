package com.sentinel.cases.exception;

public class InvalidCaseStatusTransitionException
        extends RuntimeException {

    public InvalidCaseStatusTransitionException(String message) {
        super(message);
    }
}