package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateItemRequest {

    @NotBlank(message = "Название вещи должно быть задано")
    private String name;

    @NotBlank(message = "Описание вещи должно быть задано")
    private String description;

    @NotNull(message = "Должно быть указано, дсотупна ли вещь для аренды")
    private Boolean available;

    private Long requestId;
}
