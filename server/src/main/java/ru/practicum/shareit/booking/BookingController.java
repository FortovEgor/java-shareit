package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

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
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody CreateBookingRequest request,
                                                    @RequestHeader("X-Sharer-User-Id") Long userid) throws NotFoundException, BadRequest {
        Booking booking = bookingService.createBooking(request, userid);
        return mapper.toDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                                     @RequestParam Boolean approved,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) throws ForbiddenException, NotFoundException {
        Booking booking = bookingService.approveBooking(bookingId, approved, userId);
        return mapper.toDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) throws ForbiddenException, NotFoundException {
        Booking booking = bookingService.getById(bookingId, userId);
        log.info("Booking: " + booking);
        var res = mapper.toDto(booking);
        log.info("Res: " + res);
        return res;
    }

    @GetMapping()
    public List<BookingDto> getCurrentUserBookings(@RequestParam(required = false, defaultValue = "ALL") String stateValue,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        List<Booking> bookings = bookingService.getUserBookings(stateValue, userId);
        return mapper.toDto(bookings);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(required = false, defaultValue = "ALL") String stateValue,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        List<Booking> bookings = bookingService.getOwnerBookings(stateValue, userId);
        return mapper.toDto(bookings);
    }
}
