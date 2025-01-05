package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Long id;  // для совместимости с большими значениями, генерируемыми SERIAL

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private boolean isAvailable;
    private User owner;
    private ItemRequest request;
}
