package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.NotFoundException;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingMapper mapper;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody CreateBookingRequest request,
                                                    @RequestHeader("X-Sharer-User-Id") Long userid) throws NotFoundException {
        Booking booking = bookingService.createBooking(request, userid);
        BookingDto dto = mapper.toDto(booking);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
}
