package ru.practicum.shareit.exceptions;

import lombok.Getter;

@Getter

public class ErrorResponse {
    public String error;

    public String stackTrace;

    public ErrorResponse(String error, String stackTrace) {
        this.error = error;
        this.stackTrace = stackTrace;
    }

    public ErrorResponse(String error) {
        this.error = error;
    }
}
