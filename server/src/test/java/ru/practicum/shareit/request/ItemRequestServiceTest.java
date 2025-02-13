package ru.practicum.shareit.request;

import org.hamcrest.Matchers;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Spy
    private ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestService itemRequestService;

    @Test
    @DisplayName("Получены все свои запросы вместе с данными об ответах на них, " +
            "когда вызваны по умолчанию, то получен пустой список")
    void getAllItemRequestsByUserWhenInvokedThenReturnedEmptyList() throws NotFoundException {
        Long userId = 0L;
        when(userService.getById(anyLong())).thenReturn(new User());

        List<ItemRequest> actualItemRequests = itemRequestService.getUserRequests(userId);

        assertThat(actualItemRequests, empty());
    }

    @Test
    @DisplayName("Получены все свои запросы вместе с данными об ответах на них, " +
            "когда вызваны, то получен непустой список")
    void getAllItemRequestsByUserWhenInvokedThenReturnedItemRequestsCollectionInList() throws NotFoundException {
        Long userId = 0L;
        List<ItemRequest> expectedItemRequests = List.of(new ItemRequest(), new ItemRequest());
        when(userService.getById(anyLong())).thenReturn(new User());
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(expectedItemRequests);

        List<ItemRequest> actualItemRequests = itemRequestService.getUserRequests(userId);

        assertThat(actualItemRequests, Matchers.not(Matchers.empty()));
    }

    @Test
    @DisplayName("Получены все свои запросы вместе с данными об ответах на них, " +
            "когда пользователь не найден, тогда выбрасывается исключение")
    void getAllItemRequestsByUserWhenUserNotFoundThenExceptionThrown() throws NotFoundException {
        Long userId = 0L;
        when(userService.getById(anyLong())).thenThrow(new NotFoundException("Не найден пользователь с userId = 100"));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getUserRequests(userId));

        assertThat("Не найден пользователь с userId = 100", equalTo(exception.getMessage()));
    }

    @Test
    @DisplayName("Получены все запросы, созданные другими пользователями, " +
            "когда вызваны по умолчанию, то получен пустой список")
    void getAllItemRequestsByOtherUsersWhenInvokedThenReturnedEmptyList() throws NotFoundException {
        Long userId = 0L;
        when(userService.getById(anyLong()))
                .thenReturn(new User());

        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of());
        List<ItemRequest> actualItemRequests = itemRequestService
                .getAllOtherUsersRequests(userId);


        assertThat(actualItemRequests, empty());
    }

    @Test
    @DisplayName("Получены все запросы, созданные другими пользователями, " +
            "когда вызваны, то получен непустой список")
    void getAllItemRequestsByOtherUsersWhenInvokedThenReturnItemRequestsCollectionInList() throws NotFoundException {
        Long userId = 0L;
        List<ItemRequest> expectedItemRequests = List.of(new ItemRequest(), new ItemRequest());
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyLong()))
                .thenReturn(expectedItemRequests);

        List<ItemRequest> actualItemRequests = itemRequestService
                .getAllOtherUsersRequests(userId);

        assertThat(actualItemRequests, Matchers.not(Matchers.empty()));
    }

    @Test
    @DisplayName("Получены все свои запросы вместе с данными об ответах на них, " +
            "когда пользователь не найден, тогда выбрасывается исключение")
    void getAllItemRequestsByOtherUsersWhenUserNotFoundThenExceptionThrown() throws NotFoundException {
        Long userId = 0L;
        when(userService.getById(anyLong())).thenThrow(new NotFoundException("Пользователь с id = 0 не найден."));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllOtherUsersRequests(userId));

        assertThat("Пользователь с id = 0 не найден.", equalTo(exception.getMessage()));
    }

    @Test
    void createItemRequestTest() throws NotFoundException {
        Long userId = 0L;
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(itemRequestRepository.save(any()))
                .thenReturn(new ItemRequest());

        ItemRequest actualItemRequest = itemRequestService
                .createItemRequest(new ItemRequestDto(1L, "description", Instant.now(),  null), userId);

        assertNotNull(actualItemRequest);
    }

    @Test
    void getRequestByIdTest() throws NotFoundException {
        Long userId = 0L;
        when(userService.getById(anyLong()))
                .thenReturn(new User());
        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.of(new ItemRequest()));

        ItemRequest actualItemRequest = itemRequestService
                .getRequestById(1L, userId);

        assertNotNull(actualItemRequest);
    }

    @Test
    void mapper2Test() {
        Set<ItemDto> items = new HashSet<>();
        items.add(new ItemDto(1L, "a", "b", true,
                List.of(new CommentDto(1L, "text", "author", Instant.now())),
                LocalDateTime.now(), LocalDateTime.now()));
        assertDoesNotThrow(() -> itemRequestMapper.toItemRequest(new ItemRequestDto(1L, "descr", Instant.now(), items), new User()));
    }

    @Test
    void mapper3Test() {
        Set<Item> items = new HashSet<>();
        items.add(new Item());
        assertDoesNotThrow(() -> itemRequestMapper.toDto(new ItemRequest(1L, "descr", new User(), Instant.now(), items)));
    }
}