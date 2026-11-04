package com.sentinel.player.exception;

public class InvalidPlayerRequestException extends RuntimeException {

    public InvalidPlayerRequestException(String message) {
        super(message);
    }
}