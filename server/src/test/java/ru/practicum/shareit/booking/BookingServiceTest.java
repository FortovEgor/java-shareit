package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = ShareItServer.class)
@ExtendWith(MockitoExtension.class)
@Slf4j
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemRequestService itemRequestService;
    @Mock
    private UserService userService;

    @InjectMocks
    private BookingService bookingService;

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    private User user = new User(1L, "user1", "user1@mail.ru");
    private User user2 = new User(2L, "user2", "user2@mail.ru");
    private Item item = new Item(1L, user, "Дрель", "Простая дрель", true, null, new ItemRequest(1));
    private final Item itemNotAvailable = new Item(1L, user, "Дрель", "Непростая дрель", false, null, null);
    private Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
            item, user2, BookingStatus.WAITING);
    private Booking bookingApprove = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            item, user2, BookingStatus.APPROVED);
    private Booking bookingReject = new Booking(3L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            item, user2, BookingStatus.REJECTED);

    @Test
    void getBookingByIdTest() throws ForbiddenException, NotFoundException {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        assertEquals(booking.getId(),
                bookingService.getById(booking.getId(), booking.getBooker().getId()).getId());
    }

    @Test
    void getWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingService.getById(booking.getId(), 100));
    }

    @Test
    void createBookingTest() throws NotFoundException {
        when(itemService.getById(any(Long.class)))
                .thenReturn(item);
        assertDoesNotThrow(() -> bookingService.createBooking(new CreateBookingRequest(
                1L, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(2)), 2));
    }

    @Test
    void createBookingEqualDatesTest() throws NotFoundException {
        when(itemService.getById(any(Long.class)))
                .thenReturn(item);
        LocalDateTime now = LocalDateTime.now().plusDays(2);
        assertThrows(BadRequest.class, () -> bookingService.createBooking(
                new CreateBookingRequest(1L, now, now),
                2));
    }

    @Test
    void createBookingStartAfterEndTest() throws NotFoundException {
        when(itemService.getById(any(Long.class)))
                .thenReturn(item);
        assertThrows(BadRequest.class, () -> bookingService.createBooking(
                new CreateBookingRequest(1L, LocalDateTime.now().plusDays(10),
                        LocalDateTime.now().plusDays(2)), 2));
    }

    @Test
    void createBookingNotAvailableTest() throws NotFoundException {
        when(itemService.getById(any(Long.class)))
                .thenReturn(itemNotAvailable);
        assertThrows(BadRequest.class, () -> bookingService.createBooking(
                new CreateBookingRequest(1L, LocalDateTime.now().plusMinutes(1),
                        LocalDateTime.now().plusDays(2)), 1));
    }

    @Test
    void updateBookingTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        assertDoesNotThrow(() -> bookingService.approveBooking(booking.getId(), true, user.getId()));
    }

    @Test
    void updateBookingApprovedByNotOwnerTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        assertThrows(ForbiddenException.class, () -> bookingService
                .approveBooking(booking.getId(), true, user2.getId()));
    }

    @Test
    void getAllBookingByUserIdTest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllByBookerOrderByStartAsc(any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getUserBookings("ALL", user2.getId()).size());
    }

    @Test
    void getAllBookingByUserIdCURRENTTest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartAsc(any(), any(), any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getUserBookings("CURRENT", user2.getId()).size());
    }

    @Test
    void getAllBookingByUserIdPASTTest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllByBookerAndEndBeforeOrderByStartAsc(any(), any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getUserBookings("PAST", user2.getId()).size());
    }

    @Test
    void getAllBookingByUserIdFUTURETest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllByBookerAndStartAfterOrderByStartAsc(any(), any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getUserBookings("FUTURE", user2.getId()).size());
    }

    @Test
    void getAllBookingByUserIdWAITINGTest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllByBookerAndStatusOrderByStartAsc(any(), any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getUserBookings("WAITING", user2.getId()).size());
    }

    @Test
    void getAllBookingByUserIdREJECTEDTest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllByBookerAndStatusOrderByStartAsc(any(), any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getUserBookings("REJECTED", user2.getId()).size());
    }

    @Test
    void getAllBookingByOwnerIdTest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllSortedOwnerBookings(any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getOwnerBookings("ALL", user.getId()).size());
    }

    @Test
    void getAllBookingByOwnerIdCURRENTTest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllCurrentByOwnerOrderByStartAsc(any(), any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getOwnerBookings("CURRENT", user.getId()).size());
    }

    @Test
    void getAllBookingByOwnerIdPASTTest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllPastByOwnerOrderByStartAsc(any(), any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getOwnerBookings("PAST", user.getId()).size());
    }

    @Test
    void getAllBookingByOwnerIdFUTURETest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllSortedOwnerBookings(any(), any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getOwnerBookings("FUTURE", user.getId()).size());
    }

    @Test
    void getAllBookingByOwnerIdWAITINGTest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllByOwnerAndStatusOrderByStartAsc(any(), any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getOwnerBookings("WAITING", user.getId()).size());
    }

    @Test
    void getAllBookingByOwnerIdREJECTEDTest() throws NotFoundException {
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(bookingRepository.findAllByOwnerAndStatusOrderByStartAsc(any(), any()))
                .thenReturn(List.of(booking, bookingApprove, bookingReject));
        assertEquals(3,
                bookingService.getOwnerBookings("REJECTED", user.getId()).size());
    }

    @Test
    void getUserBookingsTest() {
        Comment comment = new Comment(1L, "", new User(), new Item(), Instant.now());
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        Item item = new Item(1L, user, "Дрель", "Простая дрель", true, comments,
                new ItemRequest(1));
        assertDoesNotThrow(() -> bookingMapper.toItemDto(item));
    }

    @Test
    void existPastApprovedItemBookingByUserTest() {
        assertDoesNotThrow(() -> bookingService.existPastApprovedItemBookingByUser(new Item(), new User()));
    }

    @Test
    void getByIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(new Booking(1L, LocalDateTime.now(), LocalDateTime.now(),
                        new Item(1L, user, "Дрель",
                                "Непростая дрель", false, null, null),
                        new User(1L, "a", "b"), BookingStatus.APPROVED)));
        assertThrows(ForbiddenException.class, () -> bookingService.getById(1L, 2L));
    }
}