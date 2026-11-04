package com.sentinel.assignment.exception;

public class ModeratorAlreadyExistsException extends RuntimeException {
    public ModeratorAlreadyExistsException(String username) {
        super("Moderator already exists: " + username);
    }
}
