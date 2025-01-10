package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateUserRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @NotBlank(message = "Почта пользователя не может отсутствовать")
    @Email
    private String email;
}
