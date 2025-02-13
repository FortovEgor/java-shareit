package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название вещи должно быть задано")
    private String name;

    @NotBlank(message = "Ошибка! Развёрнутое описание вещи не может быть пустым.")
    private String description;

    @NotNull(message = "Должно быть указано, дсотупна ли вещь для аренды")
    private Boolean available;

    private Long requestId;

}
