package ru.goryacms.overlay.api.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message, null, true, false);
    }
}
