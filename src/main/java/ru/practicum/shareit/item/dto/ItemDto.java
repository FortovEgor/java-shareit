package ru.practicum.shareit.item.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
}
