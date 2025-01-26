package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // для совместимости с большими значениями, генерируемыми SERIAL

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String name;

    private String description;

    private boolean available;

    @OneToMany(mappedBy = "item")
    private List<Comment> comments = new ArrayList<>();

    @Transient
    private LocalDateTime lastBooking;

    @Transient
    private LocalDateTime nextBooking;

    public Item (Long id, User owner, String name, String description, boolean available, List<Comment> comments) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.available = available;
        if (comments != null) {
            this.comments = comments;
        }
    }
}
