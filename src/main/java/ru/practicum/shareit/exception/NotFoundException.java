package ru.practicum.shareit.exception;

public class NotFoundException extends Exception {
    public NotFoundException(String pattern, Object... replaces) {
        super(String.format(pattern, replaces));
    }
}
