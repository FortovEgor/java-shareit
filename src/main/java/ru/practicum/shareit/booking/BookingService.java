package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.User;

@Service
@Validated
@RequiredArgsConstructor
public class BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository repo;
    private final BookingMapper mapper;

    public Booking createBooking(@Valid CreateBookingRequest request, long userId) throws NotFoundException {
        User booker = userService.getById(userId);
        Item item = itemService.getById(request.getItemId());
        Booking booking = mapper.toBooking(request);
        booking.setBooker(booker);
        booking.setItem(item);
        return repo.save(booking);
    }
}
