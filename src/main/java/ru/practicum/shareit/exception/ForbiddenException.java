package ru.practicum.shareit.exception;

public class ForbiddenException extends Exception {
    public ForbiddenException(String pattern, Object... replaces) {
        super(String.format(pattern, replaces));
    }
}
