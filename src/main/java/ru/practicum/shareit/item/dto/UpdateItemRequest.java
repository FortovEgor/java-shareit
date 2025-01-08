package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import ru.practicum.shareit.validator.NullOrNotEmpty;

@Getter
public class UpdateItemRequest {

    @NullOrNotEmpty(message = "Название вещи не должно быть пустым")
    private String name;

    @NullOrNotEmpty(message = "Описание вещи не должно быть пустым")
    private String description;

    private Boolean available;
}
