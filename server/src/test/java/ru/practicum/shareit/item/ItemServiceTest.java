package ru.practicum.shareit.item;

import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private ItemService itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Test
    @DisplayName("получены все вещи, когда вызваны по умолчанию, то получен пустой список")
    void getAllItemsByUser_whenInvoked_thenReturnedNotEmptyList() throws NotFoundException {
        Long userId = 0L;
        Item item = new Item(1L, new User(), "f", "d",
                true, null, new ItemRequest());
        when(userService.getById(anyLong()))
                .thenReturn(new User(userId, "name", "email"));
        when(itemRepository.findAllByOwnerWithComments(any()))
                .thenReturn(List.of(item, new Item()));

        List<Item> actualItems = itemService.getItemsByUserId(userId);

        assertThat(actualItems.size(), equalTo(2));
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1))
                .findAllByOwnerWithComments(any());
    }

    @Test
    @DisplayName("получены все вещи, когда вызваны, то получен непустой список")
    void getAllItemsByUser_whenInvoked_thenReturnedItemsCollectionInList() throws NotFoundException {
        long userId = 0L;
        User user = new User();
        user.setId(1L);
        long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        List<Item> expectedItems = List.of(item);

        when(itemRepository.findAllByOwnerWithComments(any())).thenReturn(expectedItems);
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        List<Item> actualItems = itemService.getItemsByUserId(userId);

        assertThat(expectedItems.size(), equalTo(actualItems.size()));
        assertThat(expectedItems.get(0).getId(), equalTo(actualItems.get(0).getId()));
        assertThat(expectedItems.get(0).getName(), equalTo(actualItems.get(0).getName()));
        assertThat(expectedItems.get(0).getDescription(), equalTo(actualItems.get(0).getDescription()));
        assertThat(expectedItems.get(0).isAvailable(), equalTo(actualItems.get(0).isAvailable()));
        assertThat(expectedItems.get(0).getRequest(), equalTo(actualItems.get(0).getRequest()));

        InOrder inOrder = inOrder(itemRepository, commentRepository);
        inOrder.verify(itemRepository, times(1))
                .findAllByOwnerWithComments(any());
//        inOrder.verify(itemRepository, times(1)).findById(anyLong());
//        inOrder.verify(commentRepository, times(1)).findById(anyLong());
    }
//
//

    @Test
    @DisplayName("получена вещь по ид, когда вещь найдена, тогда она возвращается")
    void getItemById_whenItemFound_thenReturnedItem() throws NotFoundException {
        long itemId = 0L;
        long userId = 0L;
        Item expectedItem = new Item();
        User user = new User();
        user.setId(1L);
        expectedItem.setOwner(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
//        when(commentRepository.findAllById(Collections.singleton(anyLong()))).thenReturn(Collections.EMPTY_LIST);

        Item actualItem = itemService.getById(itemId);

        assertThat(expectedItem, equalTo(actualItem));
//        InOrder inOrder = inOrder(itemRepository, commentRepository);
//        inOrder.verify(itemRepository, times(1)).findById(itemId);
//        inOrder.verify(commentRepository, times(1)).findById(itemId);
    }

//    @Test
//    @DisplayName("получена вещь по ид, когда вещь найдена, тогда она возвращается с бронированиями")
//    void getItemById_whenItemFound_thenReturnedItemWithBookings() {
//        long userId = 0L;
//        User user = new User();
//        user.setId(userId);
//        long itemId = 0L;
//        Item expectedItem = new Item();
//        expectedItem.setId(itemId);
//        expectedItem.setOwner(user);
//
//        Booking lastBooking = new Booking();
//        lastBooking.setId(5L);
//        lastBooking.setStart(LocalDateTime.now());
//        lastBooking.setEnd(LocalDateTime.now().plusHours(1));
//        lastBooking.setStatus(BookingStatus.APPROVED);
//        lastBooking.setItem(expectedItem);
//        Booking nextBooking = new Booking();
//        nextBooking.setId(7L);
//        nextBooking.setStart(LocalDateTime.now());
//        nextBooking.setEnd(LocalDateTime.now().plusHours(2));
//        nextBooking.setStatus(BookingStatus.APPROVED);
//        nextBooking.setItem(expectedItem);
//
//        Comment comment = new Comment();
//        comment.setId(2L);
//        comment.setText("text");
//        comment.setCreated(Instant.now());
//        comment.setAuthor(user);
//        comment.setItem(expectedItem);
//
//        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
//        when(bookingRepository.findFirstByItemIdAndStatusAndStartIsBefore(anyLong(), any(StatusBooking.class),
//                any(LocalDateTime.class), any(Sort.class))).thenReturn(Optional.of(lastBooking));
//        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(), any(BookingStatus.class),
//                any(LocalDateTime.class), any(Sort.class))).thenReturn(Optional.of(nextBooking));
//        when(commentRepository.findAllById(any())).thenReturn(List.of(comment));
//
//        Item actualItem = itemService.getById(itemId);
//
//        assertThat(ItemMapper.INSTANCE.toItemDtoOwner(expectedItem,
//                lastBooking, nextBooking, List.of(comment)), equalTo(actualItem));
//        InOrder inOrder = inOrder(itemRepository, bookingRepository, commentRepository);
//        inOrder.verify(itemRepository, times(1)).findById(itemId);
//        inOrder.verify(bookingRepository, times(1))
//                .findFirstByItemIdAndStatusAndStartIsBefore(anyLong(), any(StatusBooking.class),
//                        any(LocalDateTime.class), any(Sort.class));
//        inOrder.verify(bookingRepository, times(1))
//                .findFirstByItemIdAndStatusAndStartIsAfter(anyLong(), any(StatusBooking.class),
//                        any(LocalDateTime.class), any(Sort.class));
//        inOrder.verify(commentRepository, times(1)).findAllByItemId(itemId);
//    }

    @Test
    @DisplayName("получена вещь по ид, когда вещь не найдена, тогда выбрасывается исключение")
    void getItemById_whenItemNotFound_thenExceptionThrown() {
        long itemId = 0L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getById(itemId));

        assertThat("не найдена вещь с id = 0", equalTo(exception.getMessage()));
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    @DisplayName("сохранена вещь, когда вещь валидна, тогда она сохраняется")
    void saveItem_whenItemValid_thenSavedItem() throws NotFoundException {
        Item itemToSave = new Item();
        itemToSave.setAvailable(true);
        Long userId = 0L;
        User user = new User();
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(itemToSave);
        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.of(new ItemRequest()));

        Item actualItem = itemService.createItem(
                new CreateItemRequest(itemToSave.getName(), itemToSave.getDescription(), itemToSave.isAvailable(), 1L), user.getId());

        assertThat(itemToSave, equalTo(actualItem));
//        InOrder inOrder = inOrder(userRepository, itemRepository);
//        inOrder.verify(userRepository, times(1)).findById(userId);
//        inOrder.verify(itemRepository, times(1)).save(any(Item.class));
    }
//
//    @Test
//    @DisplayName("сохранена вещь, когда вещь с запросом, тогда она сохраняется")
//    void saveItem_whenItemWithRequest_thenSavedItem() {
//        Long userId = 0L;
//        User user = new User();
//        ItemDto itemToSave = new ItemDto();
//        itemToSave.setAvailable(true);
//        itemToSave.setRequestId(1L);
//        ItemRequest itemRequest = new ItemRequest();
//        itemRequest.setId(1L);
//        Item item = ItemMapper.INSTANCE.toItem(itemToSave, user);
//        item.setRequest(itemRequest);
//
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
//        when(itemRepository.save(any(Item.class)))
//                .thenReturn(item);
//
//        ItemDto actualItem = itemService.saveItem(userId, itemToSave);
//
//        assertThat(itemToSave, equalTo(actualItem));
//        InOrder inOrder = inOrder(userRepository, itemRequestRepository, itemRepository);
//        inOrder.verify(userRepository, times(1)).findById(userId);
//        inOrder.verify(itemRequestRepository, times(1)).findById(1L);
//        inOrder.verify(itemRepository, times(1)).save(any(Item.class));
//    }
//
//    @Test  /// sss
//    @DisplayName("сохранена вещь, когда статус доступности не валиден, тогда выбрасывается исключение")
//    void saveItem_whenAvailableNotValid_thenExceptionThrown() {
//        Item itemToSave = new Item();
//        Long userId = 0L;
//
//        final ValidationException exception = assertThrows(ValidationException.class,
//                () -> itemService.createItem(
//                        new CreateItemRequest(itemToSave.getName(), itemToSave.getDescription(), itemToSave.isAvailable(), null),
//                        userId
//                ));
//
//        assertThat("Ошибка! Статус доступности вещи для аренды не может быть пустым. " +
//                "Код ошибки: 20001", equalTo(exception.getMessage()));
////        InOrder inOrder = inOrder(userRepository, itemRepository);
////        inOrder.verify(userRepository, never()).findById(userId);
////        inOrder.verify(itemRepository, never()).save(any(Item.class));
//    }
//
//    @Test
//    @DisplayName("сохранена вещь, когда вещь не валидна, тогда выбрасывается исключение")
//    void saveItem_whenItemNotValid_thenExceptionThrown() {
//        ItemDto itemToSave = new ItemDto();
//        itemToSave.setAvailable(true);
//        Long userId = 0L;
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
//        when(itemRepository.save(any(Item.class)))
//                .thenThrow(new DataIntegrityViolationException("Вещь не была создана"));
//
//        final NotFoundException exception = assertThrows(NotFoundException.class,
//                () -> itemService.createItem(
//                        new CreateItemRequest(itemToSave.getName(), itemToSave.getDescription(), itemToSave.isAvailable(), null),
////                        userId
//                );
//
//        assertThat("Вещь не была создана: " + itemToSave, equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(userRepository, itemRepository);
//        inOrder.verify(userRepository, times(1)).findById(userId);
//        inOrder.verify(itemRepository, times(1)).save(any(Item.class));
//    }
    ////////////////////
//
//    @Test
//    @DisplayName("сохранена вещь, когда пользователь вещи не найден, тогда выбрасывается исключение")
//    void saveItem_whenUserNotFound_thenExceptionThrown() {
//        ItemDto itemToSave = new ItemDto();
//        itemToSave.setAvailable(true);
//        Long userId = 0L;
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
//                () -> itemService.saveItem(userId, itemToSave));
//
//        assertThat("Пользователь с id = 0 не найден.", equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(userRepository, itemRepository);
//        inOrder.verify(userRepository, times(1)).findById(userId);
//        inOrder.verify(itemRepository, never()).save(any(Item.class));
//    }
//
//
//    @Test
//    @DisplayName("обновлена вещь, когда вещь валидна, тогда она обновляется")
//    void updateItem_whenItemFound_thenUpdatedItemOnlyAvailableFields() {
//        Long userId = 0L;
//        User user = new User();
//        user.setId(userId);
//        Long itemId = 0L;
//        Item oldItem = new Item();
//        oldItem.setName("1");
//        oldItem.setDescription("1");
//        oldItem.setAvailable(false);
//        oldItem.setOwner(user);
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(oldItem));
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
//
//        ItemRequest itemRequest = new ItemRequest();
//        itemRequest.setId(5L);
//        Item newItem = new Item();
//        newItem.setName("2");
//        newItem.setDescription("2");
//        newItem.setAvailable(true);
//        newItem.setRequest(itemRequest);
//
//        itemService.updateItem(userId, itemId, ItemMapper.INSTANCE.toItemDto(newItem));
//        verify(itemRepository).saveAndFlush(itemArgumentCaptor.capture());
//        Item savedItem = itemArgumentCaptor.getValue();
//
//        assertThat(newItem.getName(), equalTo(savedItem.getName()));
//        assertThat(newItem.getDescription(), equalTo(savedItem.getDescription()));
//        assertThat(newItem.getAvailable(), equalTo(savedItem.getAvailable()));
//
//        InOrder inOrder = inOrder(userRepository, itemRepository);
//        verify(itemRepository, times(1)).findById(itemId);
//        inOrder.verify(userRepository, times(1)).findById(userId);
//        inOrder.verify(itemRepository, times(1)).saveAndFlush(any(Item.class));
//    }
//

    @Test
    @DisplayName("обновлена вещь, когда вещь не найдена, тогда выбрасывается исключение")
    void updateItemTestd() {
        Long userId = 0L;
        Long itemId = 0L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(new UpdateItemRequest("name", "desc", true), 100L, 200L));

        assertThat("не найдена вещь с id = 100", equalTo(exception.getMessage()));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).findById(userId);
        verify(itemRepository, never()).saveAndFlush(any(Item.class));
    }

//
//    @Test
//    @DisplayName("обновлена вещь, когда пользователь не является владельцем вещи, " +
//            "тогда выбрасывается исключение")
//    void updateItem_whenUserNotValid_thenExceptionThrown() {
//        Long userId = 0L;
//        User user = new User();
//        user.setId(1L);
//        Long itemId = 0L;
//        Item oldItem = new Item();
//        oldItem.setOwner(user);
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(oldItem));
//
//        final ItemOtherOwnerException exception = assertThrows(ItemOtherOwnerException.class,
//                () -> itemService.updateItem(userId, itemId, new ItemDto()));
//
//        assertThat(String.format("Пользователь с id = 0 не является владельцем вещи: " + new ItemDto()),
//                equalTo(exception.getMessage()));
//        verify(itemRepository, times(1)).findById(anyLong());
//        verify(userRepository, never()).findById(userId);
//        verify(itemRepository, never()).saveAndFlush(any(Item.class));
//    }
//
//    @Test
//    @DisplayName("обновлена вещь, когда пользователь не найден, тогда выбрасывается исключение")
//    void updateItem_whenUserNotFound_thenExceptionThrown() {
//        Long userId = 0L;
//        User user = new User();
//        user.setId(userId);
//        Long itemId = 0L;
//        Item oldItem = new Item();
//        oldItem.setOwner(user);
//
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(oldItem));
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
//                () -> itemService.updateItem(userId, itemId, new ItemDto()));
//
//        assertThat("Пользователь с id = 0 не найден.", equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(itemRepository, userRepository);
//        inOrder.verify(itemRepository, times(1)).findById(anyLong());
//        inOrder.verify(userRepository, times(1)).findById(userId);
//        verify(itemRepository, never()).saveAndFlush(any(Item.class));
//    }
//
//    @Test
//    @DisplayName("обновлена вещь, когда вещь не может быть обновлена, тогда выбрасывается исключение")
//    void updateItem_whenItemNotUpdate_thenExceptionThrown() {
//        Long userId = 0L;
//        User user = new User();
//        user.setId(userId);
//        Long itemId = 0L;
//        Item oldItem = new Item();
//        oldItem.setOwner(user);
//        ItemDto itemDto = new ItemDto();
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(oldItem));
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
//        when(itemRepository.saveAndFlush(any(Item.class)))
//                .thenThrow(new DataIntegrityViolationException("Вещь не была обновлена"));
//
//        final ItemNotUpdateException exception = assertThrows(ItemNotUpdateException.class,
//                () -> itemService.updateItem(userId, itemId, itemDto));
//
//        assertThat("Вещь с id = 0 не была обновлена: " + itemDto, equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(userRepository, itemRepository);
//        verify(itemRepository, times(1)).findById(anyLong());
//        inOrder.verify(userRepository, times(1)).findById(userId);
//        inOrder.verify(itemRepository, times(1)).saveAndFlush(any(Item.class));
//    }
//

    @Test
    @DisplayName("получены все вещи по тексту, когда вызваны по умолчанию, то получен пустой список")
    void findItems_whenInvokedWithEmptyText_thenReturnedList() throws NotFoundException {
        Long userId = 0L;
//
//        when(itemRepository.getLastAndNextBookingDate(any(), any()))
//                .then()
        when(itemRepository.findAllByOwnerWithComments(any()))
                .thenReturn(List.of(new Item()));
//        List<Item> actualItems =itemService.getItemsByUserId(userId) ;

        assertDoesNotThrow(() -> itemService.getItemsByUserId(userId));
        verify(itemRepository, never()).search("");
    }

    @Test
    @DisplayName("получены все вещи по тексту, когда вызваны по умолчанию, то получен пустой список")
    void findItems_whenInvoked_thenReturnedEmptyList() throws NotFoundException {
        Long userId = 0L;

        List<Item> actualItems = itemService.getItemsByUserId(userId);

        assertThat(actualItems, empty());
//        verify(itemRepository, times(1))
//                .search("1");
    }

    @Test
    void deleteTest() throws NotFoundException, ForbiddenException {
        User user = new User(1L, "name", "email");
        Item item = new Item(1L, user, "a", "b", true, null, null);
        when(userService.getById(anyLong()))
                .thenReturn(user);
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        assertDoesNotThrow(() -> itemService.deleteById(1L, 2L));
    }

    @Test
    void updateItemTest() throws NotFoundException {
        User user = new User(1L, "name", "email");
        Item item = new Item(1L, user, "a", "b", true, null, null);
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(userService.getById(anyLong()))
                .thenReturn(user);
        assertDoesNotThrow(() -> itemService.updateItem(new UpdateItemRequest("name", "description", true), item.getId(), user.getId()));
    }

    @Test
    void deleteByIdTest() throws NotFoundException {
        User user = new User(1L, "name", "email");
        Item item = new Item(1L, user, "a", "b", true, null, null);
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(userService.getById(anyLong()))
                .thenReturn(user);
        assertDoesNotThrow(() -> itemService.deleteById(item.getId(), user.getId()));
    }

    @Test
    void deleteByIdFailTest() throws NotFoundException {
        User user = new User(1L, "name", "email");
        User second = new User(2L, "name", "email");
        Item item = new Item(1L, user, "a", "b", true, null, null);
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(userService.getById(anyLong()))
                .thenReturn(second);
        assertThrows(ForbiddenException.class, () -> itemService.deleteById(item.getId(), user.getId()));
    }


//    @Test
//    @DisplayName("получены все вещи по тексту, когда вызваны, то получен непустой список")
//    void findItems_whenInvoked_thenReturnedItemsCollectionInList() throws NotFoundException {
//        Long userId = 0L;
//        List<Item> expectedItems = List.of(new Item(), new Item());
//        when(itemRepository.search(anyString())).thenReturn(expectedItems);
//
//        List<Item> actualItems = itemService.getItemsByUserId(userId);
//
//        assertThat(expectedItems, equalTo(actualItems));
//        verify(itemRepository, times(1))
//                .search("1");
//    }
//
//    @Test
//    @DisplayName("сохранен комментарий, когда комментарий валиден, тогда он сохраняется")
//    void saveComment_whenCommentValid_thenSavedComment() {
//        Comment comment = new Comment();
//        CommentDto commentToSave = CommentMapper.INSTANCE.toCommentDto(comment);
//        Long userId = 0L;
//        User user = new User();
//        Long itemId = 0L;
//        Item item = new Item();
//
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//        when(bookingRepository.isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
//                .thenReturn(0L);
//        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
//
//        CommentDto actualComment = itemService.saveComment(userId, itemId, commentToSave);
//
//        assertThat(commentToSave, equalTo(actualComment));
//        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository, commentRepository);
//        inOrder.verify(itemRepository, times(1)).findById(itemId);
//        inOrder.verify(userRepository, times(1)).findById(userId);
//        inOrder.verify(bookingRepository, times(1))
//                .isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class));
//        inOrder.verify(commentRepository, times(1)).save(any(Comment.class));
//    }
//
//    @Test
//    @DisplayName("сохранен комментарий, когда вещь не найдена, тогда выбрасывается исключение")
//    void saveComment_whenItemNotFound_thenExceptionThrown() {
//        CommentDto commentToSave = new CommentDto();
//        Long itemId = 0L;
//        Long userId = 0L;
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        final ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
//                () -> itemService.saveComment(userId, itemId, commentToSave));
//
//        assertThat("Вещь с идентификатором 0 не найдена.", equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository, commentRepository);
//        inOrder.verify(itemRepository, times(1)).findById(itemId);
//        inOrder.verify(userRepository, never()).findById(userId);
//        inOrder.verify(bookingRepository, never())
//                .isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class));
//        inOrder.verify(commentRepository, never()).save(any(Comment.class));
//    }
//
//    @Test
//    @DisplayName("сохранен комментарий, когда пользователь не найден, тогда выбрасывается исключение")
//    void saveComment_whenUserNotFound_thenExceptionThrown() {
//        CommentDto commentToSave = new CommentDto();
//        Long itemId = 0L;
//        Long userId = 0L;
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(new Item()));
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
//                () -> itemService.saveComment(userId, itemId, commentToSave));
//
//        assertThat("Пользователь с id = 0 не найден.", equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository, commentRepository);
//        inOrder.verify(itemRepository, times(1)).findById(itemId);
//        inOrder.verify(userRepository, times(1)).findById(userId);
//        inOrder.verify(bookingRepository, never())
//                .isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class));
//        inOrder.verify(commentRepository, never()).save(any(Comment.class));
//    }
//
//    @Test
//    @DisplayName("сохранен комментарий, когда комментарий не сохранен, тогда выбрасывается исключение")
//    void saveComment_whenCommentNotSaved_thenExceptionThrown() {
//        CommentDto commentToSave = new CommentDto();
//        Long itemId = 0L;
//        Long userId = 0L;
//
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(new Item()));
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
//        when(bookingRepository.isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
//                .thenReturn(0L);
//        when(commentRepository.save(any(Comment.class)))
//                .thenThrow(new DataIntegrityViolationException("Комментарий не был создан"));
//
//        final CommentNotSaveException exception = assertThrows(CommentNotSaveException.class,
//                () -> itemService.saveComment(userId, itemId, commentToSave));
//
//        assertThat("Комментарий не был создан: " + commentToSave, equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository, commentRepository);
//        inOrder.verify(itemRepository, times(1)).findById(itemId);
//        inOrder.verify(userRepository, times(1)).findById(userId);
//        inOrder.verify(bookingRepository, times(1))
//                .isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class));
//        inOrder.verify(commentRepository, times(1)).save(any(Comment.class));
//    }
//
//    @Test
//    @DisplayName("сохранен комментарий, когда комментарий не валиден, тогда выбрасывается исключение")
//    void saveComment_whenCommentNotValid_thenExceptionThrown() {
//        CommentDto commentToSave = new CommentDto();
//        Long itemId = 0L;
//        Long userId = 0L;
//
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(new Item()));
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
//        when(bookingRepository.isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
//                .thenReturn(null);
//
//        final ValidationException exception = assertThrows(ValidationException.class,
//                () -> itemService.saveComment(userId, itemId, commentToSave));
//
//        assertThat("Ошибка!  Отзыв может оставить только тот пользователь, который брал эту вещь в аренду, " +
//                "и только после окончания срока аренды. Код ошибки: 20002", equalTo(exception.getMessage()));
//        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository, commentRepository);
//        inOrder.verify(itemRepository, times(1)).findById(itemId);
//        inOrder.verify(userRepository, times(1)).findById(userId);
//        inOrder.verify(bookingRepository, times(1))
//                .isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class));
//        inOrder.verify(commentRepository, never()).save(any(Comment.class));
//    }

    @Test
    void mapperTest() {
        assertDoesNotThrow(() -> itemMapper.toCommentDto(new Comment(1L, "", new User(), new Item(), Instant.now())));
    }

    @Test
    void mapperFailTest() {
        assertDoesNotThrow(() -> itemMapper.toComment(null));
    }

    @Test
    void mapperFail2Test() {
        assertDoesNotThrow(() -> itemMapper.toDto((Item) null));
    }

    @Test
    void mapperFail3Test() {
        assertDoesNotThrow(() -> itemMapper.toDto((List) null));
    }

    @Test
    void mapperListTest() {
        assertDoesNotThrow(() -> itemMapper.toCommentDto(List.of(new Comment(1L, "", new User(), new Item(), Instant.now()))));
    }

    @Test
    void mapperItemDtoTest() {
        assertDoesNotThrow(() -> itemMapper.toDto(new Item()));
    }

    @Test
    void mapperItemDtoListTest() {
        assertDoesNotThrow(() -> itemMapper.toDto(List.of(new Item())));
    }

    @Test
    void search() {
        assertDoesNotThrow(() -> itemService.search(""));
    }

//    @Test void search22() {
//        try {
//
//        }
//        when(itemRepository.search(anyString()))
//                .thenReturn((List<Item>) new Item());
//        assertDoesNotThrow(() -> itemService.search("111"));
//    }

    @Test
    void searchFail() {
        assertEquals(null, itemMapper.toCommentDto((Comment) null));
    }

    @Test
    void searchFail2() {
        assertEquals(null, itemMapper.toCommentDto((List) null));
    }

    @Test
    void searchFail3() {
        assertDoesNotThrow(() -> itemMapper.toCommentDto(new Comment(
                1L, "text", null, new Item(),
                Instant.now())));
    }

    @Test
    void searchFail4() {
        assertDoesNotThrow(() -> itemMapper.toItem(null));
    }
}