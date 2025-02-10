package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository repo;
    private final BookingMapper mapper;

    @Transactional
    public Booking createBooking(@Valid CreateBookingRequest request, long userId) throws NotFoundException, BadRequest {
        log.info("creating booking = {}", request);

        User booker = userService.getById(userId);
        Item item = itemService.getById(request.getItemId());

        if (request.getStart().isAfter(request.getEnd())) {
            throw new BadRequest("Начало бронирования не должно быть после его окончания");
        }

        if (request.getStart().isEqual(request.getEnd())) {
            throw new BadRequest("Начало бронирования не должно совпадать с его окончанием");
        }

        if (!item.isAvailable()) {
            throw new BadRequest("Вещь с id = %s недоступна для бронирования", item.getId());
        }


        Booking booking = mapper.toBooking(request);
        booking.setBooker(booker);
        booking.setItem(item);

        Booking createdBooking = repo.save(booking);
        log.info("created booking {}", createdBooking);

        return createdBooking;
    }

    @Transactional
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

    List<Booking> getUserBookings(String stateValue, long userId) throws NotFoundException {
        log.info("getting bookings for user {} with state {}", userId, stateValue);

        LocalDateTime now = LocalDateTime.now();

        FilterBookingState state = FilterBookingState.valueOf(stateValue);
        User user = userService.getById(userId);

        List<Booking> bookings = switch (state) {
            case ALL -> repo.findAllByBookerOrderByStartAsc(user);
            case CURRENT -> repo.findAllByBookerAndStartBeforeAndEndAfterOrderByStartAsc(user, now, now);
            case PAST -> repo.findAllByBookerAndEndBeforeOrderByStartAsc(user, now);
            case FUTURE -> repo.findAllByBookerAndStartAfterOrderByStartAsc(user, now);
            case WAITING -> repo.findAllByBookerAndStatusOrderByStartAsc(user, BookingStatus.WAITING);
            case REJECTED -> repo.findAllByBookerAndStatusOrderByStartAsc(user, BookingStatus.REJECTED);
        };

        log.info("found {} booking(s)", bookings.size());

        return bookings;
    }

    public List<Booking> getOwnerBookings(String stateValue, long userId) throws NotFoundException {
        log.info("getting owner bookings...");

        LocalDateTime now = LocalDateTime.now();

        FilterBookingState state = FilterBookingState.valueOf(stateValue);
        User owner = userService.getById(userId);

        List<Booking> bookings = switch (state) {
            case ALL -> repo.findAllSortedOwnerBookings(owner);
            case CURRENT -> repo.findAllCurrentByOwnerOrderByStartAsc(owner, now);
            case PAST -> repo.findAllPastByOwnerOrderByStartAsc(owner, now);
            case FUTURE -> repo.findAllSortedOwnerBookings(owner, now);
            case WAITING -> repo.findAllByOwnerAndStatusOrderByStartAsc(owner, BookingStatus.WAITING);
            case REJECTED -> repo.findAllByOwnerAndStatusOrderByStartAsc(owner, BookingStatus.REJECTED);
        };

        log.info("found {} booking(s)", bookings.size());

        return bookings;
    }

    public boolean existPastApprovedItemBookingByUser(Item item, User user) {
        return repo.existsByItemAndBookerAndEndBefore(item, user, LocalDateTime.now());
    }
}
