package ru.practicum.shareit.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String pattern, Object... replaces) {
        super(String.format(pattern, replaces));
    }
}
