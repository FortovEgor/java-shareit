package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @NotEmpty(message = "Ошибка! e-mail не может быть пустым.")
    @Email(message = "Ошибка! Неверный e-mail.")
    private String email;

}
