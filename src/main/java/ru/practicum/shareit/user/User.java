package ru.practicum.shareit.user;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}
