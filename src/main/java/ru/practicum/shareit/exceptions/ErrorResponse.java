package ru.practicum.shareit.exceptions;

import lombok.Getter;

@Getter

public class ErrorResponse {
    public String message;

    public String stackTrace;

    public ErrorResponse(String message, String stackTrace) {
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public ErrorResponse(String message) {
        this.message = message;
    }
}
