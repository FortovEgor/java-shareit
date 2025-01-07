package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateItemRequest {

    @NotBlank(message = "Название вещи не должно быть пустым")
    private String name;

    @NotBlank(message = "Описание вещи не должно быть пустым")
    private String description;

    private Boolean available;
}
