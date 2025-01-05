package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
public class User {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}
