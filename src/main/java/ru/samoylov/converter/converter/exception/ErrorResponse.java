package ru.samoylov.converter.converter.exception;

public class ErrorResponse {
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;

    }

    public String getMessage() {
        return message;
    }

}
