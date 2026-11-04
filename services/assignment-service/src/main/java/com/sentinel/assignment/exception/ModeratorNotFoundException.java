package com.sentinel.assignment.exception;

public class ModeratorNotFoundException extends RuntimeException {
    public ModeratorNotFoundException(String id) {
        super("Moderator not found: " + id);
    }
}