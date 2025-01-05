package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
public class Item {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private boolean isAvailable;
    private User owner;
    private String request;
}
