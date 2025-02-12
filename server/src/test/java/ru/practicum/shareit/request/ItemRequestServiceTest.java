package ru.practicum.shareit.request;

import org.hamcrest.Matchers;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.Instant;
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
    @DisplayName("получены все свои запросы вместе с данными об ответах на них, " +
            "когда вызваны по умолчанию, то получен пустой список")
    void getAllItemRequestsByUser_whenInvoked_thenReturnedEmptyList() throws NotFoundException {
        Long userId = 0L;
        when(userService.getById(anyLong())).thenReturn(new User());

        List<ItemRequest> actualItemRequests = itemRequestService.getUserRequests(userId);

        assertThat(actualItemRequests, empty());
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
//        inOrder.verify(userRepository, times(1)).findById(anyLong());
//        inOrder.verify(itemRequestRepository, times(1))
//                .findAllByRequestorId(anyLong());
    }

    @Test
    @DisplayName("получены все свои запросы вместе с данными об ответах на них, " +
            "когда вызваны, то получен непустой список")
    void getAllItemRequestsByUser_whenInvoked_thenReturneItemRequestsCollectionInList() throws NotFoundException {
        Long userId = 0L;
        List<ItemRequest> expectedItemRequests = List.of(new ItemRequest(), new ItemRequest());
        when(userService.getById(anyLong())).thenReturn(new User());
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(expectedItemRequests);

        List<ItemRequest> actualItemRequests = itemRequestService.getUserRequests(userId);

        assertThat(actualItemRequests, Matchers.not(Matchers.empty()));
//        assertThat(ItemRequestMapper.INSTANCE.convertItemRequestListToItemRequestDTOList(expectedItemRequests),
//                equalTo(actualItemRequests));
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
//        inOrder.verify(userRepository, times(1)).findById(anyLong());
//        inOrder.verify(itemRequestRepository, times(1)).findAllByRequestorId(anyLong());
    }
//

    @Test
    @DisplayName("получены все свои запросы вместе с данными об ответах на них, " +
            "когда пользователь не найден, тогда выбрасывается исключение")
    void getAllItemRequestsByUser_whenUserNotFound_thenExceptionThrown() throws NotFoundException {
        Long userId = 0L;
        when(userService.getById(anyLong())).thenThrow(new NotFoundException("Не найден пользователь с userId = 100"));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getUserRequests(userId));

        assertThat("Не найден пользователь с userId = 100", equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
//        inOrder.verify(userRepository, times(1)).findById(anyLong());
//        inOrder.verify(itemRequestRepository, never()).findAllByRequestorId(anyLong());
    }

    @Test
    @DisplayName("получены все запросы, созданные другими пользователями, " +
            "когда вызваны по умолчанию, то получен пустой список")
    void getAllItemRequestsByOtherUsers_whenInvoked_thenReturnedEmptyList() throws NotFoundException {
        Long userId = 0L;
        when(userService.getById(anyLong()))
                .thenReturn(new User());

        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of());
        List<ItemRequest> actualItemRequests = itemRequestService
                .getAllOtherUsersRequests(userId);


        assertThat(actualItemRequests, empty());
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
//        inOrder.verify(userRepository, times(1)).findById(anyLong());
//        inOrder.verify(itemRequestRepository, times(1))
//                .findAllByRequestorIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("получены все запросы, созданные другими пользователями, " +
            "когда вызваны, то получен непустой список")
    void getAllItemRequestsByOtherUsers_whenInvoked_thenReturneItemRequestsCollectionInList() throws NotFoundException {
        Long userId = 0L;
        List<ItemRequest> expectedItemRequests = List.of(new ItemRequest(), new ItemRequest());
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyLong()))
                .thenReturn(expectedItemRequests);

        List<ItemRequest> actualItemRequests = itemRequestService
                .getAllOtherUsersRequests(userId);

//        assertThat(ItemRequestMapper.INSTANCE.convertItemRequestListToItemRequestDTOList(expectedItemRequests),
//                equalTo(actualItemRequests));
        assertThat(actualItemRequests, Matchers.not(Matchers.empty()));
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
//        inOrder.verify(userRepository, times(1)).findById(anyLong());
//        inOrder.verify(itemRequestRepository, times(1))
//                .findAllByRequestorIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("получены все свои запросы вместе с данными об ответах на них, " +
            "когда пользователь не найден, тогда выбрасывается исключение")
    void getAllItemRequestsByOtherUsers_whenUserNotFound_thenExceptionThrown() throws NotFoundException {
        Long userId = 0L;
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userService.getById(anyLong())).thenThrow(new NotFoundException("Пользователь с id = 0 не найден."));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllOtherUsersRequests(userId));

        assertThat("Пользователь с id = 0 не найден.", equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
//        inOrder.verify(userRepository, times(1)).findById(anyLong());
//        inOrder.verify(itemRequestRepository, never())
//                .findAllByRequestorIdNot(anyLong(), any(Pageable.class));
    }
//
//    @Test
//    @DisplayName("получен запрос по ид, когда запрос найден, тогда он возвращается")
//    void getItemRequestById_whenItemRequestFound_thenReturnedUser() {
//        long userId = 0L;
//        long itemRequestId = 0L;
//        ItemRequest expectedItemRequest = new ItemRequest();
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
//        when(itemRequestRepository.findById(userId)).thenReturn(Optional.of(expectedItemRequest));
//
//        ItemRequestDto actualItemRequest = itemRequestService.getItemRequestById(userId, itemRequestId);
//
//        assertThat(ItemRequestMapper.INSTANCE.toItemRequestDto(expectedItemRequest), equalTo(actualItemRequest));
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
//        inOrder.verify(userRepository, times(1)).findById(anyLong());
//        inOrder.verify(itemRequestRepository, times(1)).findById(itemRequestId);
//    }
//
//    @Test
//    @DisplayName("получен запрос по ид, когда пользователь не найден, тогда выбрасывается исключение")
//    void getItemRequestById_whenUserNotFound_thenExceptionThrown() {
//        long userId = 0L;
//        long itemRequestId = 0L;
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
//                () -> itemRequestService.getItemRequestById(userId, itemRequestId));
//
//        assertThat("Пользователь с id = 0 не найден.", equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
//        inOrder.verify(userRepository, times(1)).findById(anyLong());
//        inOrder.verify(itemRequestRepository, never()).findById(itemRequestId);
//    }
//
//    @Test
//    @DisplayName("получен запрос по ид, когда запрос не найден, " +
//            "тогда выбрасывается исключение")
//    void getItemRequestById_whenItemRequestNotFound_thenExceptionThrown() {
//        long itemRequestId = 0L;
//        long userId = 0L;
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
//        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        final ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
//                () -> itemRequestService.getItemRequestById(userId, itemRequestId));
//
//        assertThat("Запрос с идентификатором 0 не найден.", equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
//        inOrder.verify(userRepository, times(1)).findById(anyLong());
//        inOrder.verify(itemRequestRepository, times(1)).findById(anyLong());
//    }
//
//    @Test
//    @DisplayName("сохранен запрос, когда пользователь не найден, тогда выбрасывается исключение")
//    void saveItemRequest_whenUserNotFound_thenExceptionThrown() {
//        long userId = 0L;
//        ItemRequestDto itemRequestToSave = new ItemRequestDto();
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
//                () -> itemRequestService.saveItemRequest(userId, itemRequestToSave));
//
//        assertThat("Пользователь с id = 0 не найден.", equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
//        inOrder.verify(userRepository, times(1)).findById(anyLong());
//        inOrder.verify(itemRequestRepository, never()).save(any(ItemRequest.class));
//    }

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
//        assertDoesNotThrow();
//        assertThat(String.valueOf(actualItemRequest), equals(new ItemRequest()));
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
//
//    @Test
//    @DisplayName("сохранен запрос, когда запрос не валиден, тогда выбрасывается исключение")
//    void saveItemRequest_whenItemRequestNotValid_thenExceptionThrown() {
//        long userId = 0L;
//        ItemRequestDto itemRequestToSave = new ItemRequestDto();
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
//        when(itemRequestRepository.save(any(ItemRequest.class)))
//                .thenThrow(new DataIntegrityViolationException("Запрос не был создан"));
//
//        final ItemRequestNotSaveException exception = assertThrows(ItemRequestNotSaveException.class,
//                () -> itemRequestService.saveItemRequest(userId, itemRequestToSave));
//
//        assertThat("Запрос вещи не был создан: " + itemRequestToSave, equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
//        inOrder.verify(userRepository, times(1)).findById(anyLong());
//        inOrder.verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
//    }

    @Test
    void mapper2Test() {
        Set<ItemDto> items = new HashSet<>();
        items.add(new ItemDto());
        assertDoesNotThrow(() -> itemRequestMapper.toItemRequest(new ItemRequestDto(1L, "descr", Instant.now(), items), new User()));
    }
}