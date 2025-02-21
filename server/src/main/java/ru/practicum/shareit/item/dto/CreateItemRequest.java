package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class CreateItemRequest {

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
