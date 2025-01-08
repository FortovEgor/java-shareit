package ru.practicum.shareit.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String pattern, Object... replaces) {
        super(String.format(pattern, replaces));
    }
}
