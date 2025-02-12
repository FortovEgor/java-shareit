package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserRequest;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = ShareItServer.class)
@Transactional
class BookingServiceTest {
    private final BookingRepository bookingRepository;

    private final BookingService bookingService;

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    private User user = new User(1L, "user1", "user1@mail.ru");
    private User user2 = new User(2L, "user2", "user2@mail.ru");
    private Item item = new Item(1L, user, "Дрель", "Простая дрель", true, null, new ItemRequest(1));
//    private final Item itemNotAvailable = new Item(1L, user, "Дрель", "Непростая дрель", false, null, null);
    private Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
            item, user2, BookingStatus.WAITING);
    private Booking bookingApprove = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            item, user2, BookingStatus.APPROVED);
    private Booking bookingReject = new Booking(3L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            item, user2, BookingStatus.REJECTED);

    @Autowired
    public BookingServiceTest(BookingRepository bookingRepository,
                              BookingService bookingService,
                              ItemService itemService,
                              ItemRequestService itemRequestService,
                              UserService userService) throws ConflictException, NotFoundException {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        try {
            userService.createUser(new CreateUserRequest(user.getName(), user.getEmail()));
            userService.createUser(new CreateUserRequest(user2.getName(), user2.getEmail()));
            itemRequestService.createItemRequest(new ItemRequestDto(1L, "test Request", Instant.now(), null), user.getId());
            itemService.createItem(new CreateItemRequest(item.getName(), item.getDescription(), item.isAvailable(),
                    (item.getRequest() == null) ? null : item.getRequest().getId()), user.getId());
            bookingRepository.save(booking);
            bookingRepository.save(bookingApprove);
            bookingRepository.save(bookingReject);
        } catch (Exception ignored) { }
    }

    @BeforeEach
    void setup() {
        user = new User(1L, "user1", "user1@mail.ru");
        user2 = new User(2L, "user2", "user2@mail.ru");
        item = new Item(1L, user, "Дрель", "Простая дрель", true, null, new ItemRequest(1));
        booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                item, user2, BookingStatus.WAITING);
        bookingApprove = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user2, BookingStatus.APPROVED);
        bookingApprove = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user2, BookingStatus.APPROVED);
        bookingReject = new Booking(3L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user2, BookingStatus.REJECTED);
    }


    @Test
    void getBookingByIdTest() throws ForbiddenException, NotFoundException {
        assertEquals(booking.getId(),
                bookingService.getById(booking.getId(), booking.getBooker().getId()).getId());
    }

    @Test
    void getWrongUserTest() {
        assertThrows(ForbiddenException.class, () -> bookingService.getById(booking.getId(), 100));
    }

    @Test
    void createBookingTest() throws BadRequest, NotFoundException {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(itemMapper.toDto(item))
                .build();
        assertThrows(ConstraintViolationException.class, () -> bookingService.createBooking(new CreateBookingRequest(
                bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd()), 2));
    }

    @Test
    void createBookingStartInThePastTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(new ItemDto())
                .build();

        assertThrows(ConstraintViolationException.class, () -> bookingService.createBooking(
                new CreateBookingRequest(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2)),
                2));
    }

    @Test
    void createBookingStartAfterEndTest() {
        assertThrows(BadRequest.class, () -> bookingService.createBooking(
                new CreateBookingRequest(1L, LocalDateTime.now().plusDays(10),
                        LocalDateTime.now().plusDays(2)), 2));
    }

    @Test
    void createBookingEndInThePastTest() {
        assertThrows(ConstraintViolationException.class, () -> bookingService.createBooking(
                new CreateBookingRequest(1L, LocalDateTime.now().plusMinutes(1),
                        LocalDateTime.now().minusDays(2)), 2));
    }

    @Test  /////////////////////////////////
    void createBookingAvailableTest() {
        assertDoesNotThrow(() -> bookingService.createBooking(
                new CreateBookingRequest(1L, LocalDateTime.now().plusMinutes(1),
                        LocalDateTime.now().plusDays(2)), 1));
    }

//    @Test
//    void createBookingByOwnerTest() {
//        assertThrows(BadRequest.class, () -> bookingService.createBooking(
//                new CreateBookingRequest(1L, LocalDateTime.now().plusMinutes(1),
//                        LocalDateTime.now().plusDays(2)), 1));
//    }

    @Test
    void updateBookingTest() throws ForbiddenException, NotFoundException {
        bookingService.approveBooking(booking.getId(), true, user.getId());
        assertEquals(BookingStatus.APPROVED, bookingRepository.findById(booking.getId()).orElseThrow().getStatus());
    }

    @Test
    void updateBooking2Test() throws ForbiddenException, NotFoundException {
        bookingService.approveBooking(booking.getId(), false, user.getId());
        assertEquals(BookingStatus.REJECTED, bookingRepository.findById(booking.getId()).orElseThrow().getStatus());
    }

//    @Test
//    void updateBookingAlreadyApprovedTest() {
//        assertThrows(NotFoundException.class, () -> bookingService
//                .approveBooking(bookingApprove.getId(), true, user.getId()));
//    }

//    @Test
//    void updateBookingAlreadyRejectedTest() {
//        assertThrows(NotFoundException.class, () -> bookingService
//                .approveBooking(bookingReject.getId(), true, user.getId()));
//    }

    @Test
    void updateBookingApprovedByNotOwnerTest() {
        assertThrows(ForbiddenException.class, () -> bookingService
                .approveBooking(booking.getId(), true, user2.getId()));
    }

    @Test
    void getAllBookingByUserIdTest() throws NotFoundException {
        assertEquals(3,
                bookingService.getUserBookings("ALL", user2.getId()).size());
    }

    @Test
    void getPastBookingByUserIdTest() throws NotFoundException {
        assertEquals(new ArrayList<>(),
                bookingService.getUserBookings("PAST", user2.getId()));
    }

    @Test
    void getFutureBookingByUserIdTest() throws NotFoundException {
        assertEquals(new ArrayList<>(),
                bookingService.getUserBookings("FUTURE", user2.getId()));
    }

    @Test
    void getCurrentBookingByUserIdTest() throws NotFoundException {
        assertEquals(3,
                bookingService.getUserBookings("CURRENT", user2.getId()).size());
    }

    @Test
    void getWaitingBookingByUserIdTest() throws NotFoundException {
        assertEquals(List.of(booking).get(0).getId(),
                bookingService.getUserBookings("WAITING", user2.getId()).get(0).getId());
    }

    @Test
    void getRejectedBookingByUserIdTest() throws NotFoundException {
        assertEquals(List.of(bookingReject).get(0).getId(),
                bookingService.getUserBookings("REJECTED", user2.getId()).get(0).getId());
    }

//    @Test
//    void getAllBookingByUserIdNegativeTest() {
//        assertThrows(IllegalArgumentException.class, () -> bookingService
//                .getUserBookings("ALL", user2.getId()));
//    }

    @Test
    void getAllBookingByUserIdBadWithoutBookingTest() {
        assertThrows(IllegalArgumentException.class, () -> bookingService
                .getOwnerBookings("BAD_STATE", user2.getId()).get(0).getId());
    }

    @Test
    void getAllBookingByOwnerIdTest() throws NotFoundException {
        assertEquals(3,
                bookingService.getOwnerBookings("ALL", user.getId()).size());
    }

    @Test
    void getPastBookingByOwnerIdTest() throws NotFoundException {
        assertEquals(new ArrayList<>(),
                bookingService.getOwnerBookings("PAST", user.getId()));
    }

    @Test
    void getFutureBookingByOwnerIdTest() throws NotFoundException {
        assertEquals(new ArrayList<>(),
                bookingService.getOwnerBookings("FUTURE", user.getId()));
    }

    @Test
    void getCurrentBookingByOwnerIdTest() throws NotFoundException {
        assertEquals(3,
                bookingService.getOwnerBookings("CURRENT", user.getId()).size());
    }

    @Test
    void getWaitingBookingByOwnerIdTest() throws NotFoundException {
        assertEquals(List.of(booking).get(0).getId(),
                bookingService.getOwnerBookings("WAITING", user.getId()).get(0).getId());
    }

    @Test
    void getRejectedBookingByOwnerIdTest() throws NotFoundException {
        assertEquals(List.of(bookingReject).get(0).getId(),
                bookingService.getOwnerBookings("REJECTED", user.getId()).get(0).getId());
    }

//    @Test
//    void getAllBookingByOwnerIdNegativeTest() {
//        assertThrows(NotFoundException.class, () -> bookingService
//                .getOwnerBookings("ALL", user.getId()));
//    }

    @Test
    void getAllBookingByOwnerIdBadStateTest() {
        assertThrows(IllegalArgumentException.class, () -> bookingService
                .getOwnerBookings("BAD_STATE", user.getId()).get(0).getId());
    }

    @Test
    void getAllBookingByOwnerIdNullStateTest() throws NotFoundException {
        assertThrows(NullPointerException.class,
                () -> bookingService.getOwnerBookings(null, user.getId()).size());
    }
}