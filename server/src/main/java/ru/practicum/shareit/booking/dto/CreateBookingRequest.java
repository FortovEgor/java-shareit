package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequest {

    @NotNull
    private Long itemId;

    @NotNull
    @Future(message = "дата начала бронирования должны быть в будущем")
    private LocalDateTime start;

    @NotNull
    @Future(message = "дата начала бронирования должны быть в будущем")
    private LocalDateTime end;
}
