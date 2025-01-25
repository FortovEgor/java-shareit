package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.User;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository repo;
    private final BookingMapper mapper;

    public Booking createBooking(@Valid CreateBookingRequest request, long userId) throws NotFoundException, BadRequest {
        log.info("creating booking = {}", request);

        if (request.getStart().isAfter(request.getEnd())) {
            throw new BadRequest("Начало бронирования не должно быть после его окончания");
        }

        if (request.getStart().isEqual(request.getEnd())) {
            throw new BadRequest("Начало бронирования не должно совпадать с его окончанием");
        }

        Item item = itemService.getById(request.getItemId());

        if (!item.isAvailable()) {
            throw new BadRequest("Вещь с id = %s недоступна для бронирования", item.getId());
        }

        User booker = userService.getById(userId);
        Booking booking = mapper.toBooking(request);
        booking.setBooker(booker);
        booking.setItem(item);

        Booking createdBooking = repo.save(booking);
        log.info("created booking {}", createdBooking);

        return createdBooking;
    }

    public Booking approveBooking(Long bookingId, boolean approved, long userId) throws NotFoundException, ForbiddenException {
        log.info("approving booking = {}", bookingId);

        Booking booking = repo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("не найдено бронирование с id = %s", bookingId));

        Long ownerId = booking.getItem().getOwner().getId();
        if (!ownerId.equals(userId)) {
            throw new ForbiddenException("Пользователю с id = %d запрещено вносить изменения в вещь с id = %d: " +
                    "id ее владельца = %d", userId, bookingId, ownerId);
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("У вещи с id = {} поменялся статус на {}", bookingId, booking.getStatus());
        return booking;
    }

    public Booking getById(long bookingId, long userId) throws NotFoundException, ForbiddenException {
        log.info("getting booking by id");
        Booking booking = repo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не удалось найти бронирование с id = %d", bookingId));
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw new ForbiddenException("Пользователь с id = {} не может посмотреть бронирование с id = {}", userId, bookingId);
        }

       return booking;
    }
}
