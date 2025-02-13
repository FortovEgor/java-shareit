package ru.practicum.shareit.item.dto;


import java.time.LocalDateTime;

public interface ItemLastNextBookDate {
    Long getId();

    LocalDateTime getLastBooking();

    LocalDateTime getNextBooking();
}
