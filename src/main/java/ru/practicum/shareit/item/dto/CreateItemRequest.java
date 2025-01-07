package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateItemRequest {

    @NotNull(message = "Название вещи должно быть задано")
    private String name;

    @NotNull(message = "Описание вещи должно быть задано")
    private String description;

    @NotNull(message = "Должно быть указано, дсотупна ли вещь для аренды")
    private Boolean available;
}
