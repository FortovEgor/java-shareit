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

    @Test
    void getAllItemsByUserWhenInvokedThenReturnedNotEmptyList() throws NotFoundException {
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
    void getAllItemsByUserWhenInvokedThenReturnedItemsCollectionInList() throws NotFoundException {
        long userId = 0L;
        User user = new User();
        user.setId(1L);
        long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        List<Item> expectedItems = List.of(item);

        when(itemRepository.findAllByOwnerWithComments(any())).thenReturn(expectedItems);

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
    }

    @Test
    void getItemByIdWhenItemFoundThenReturnedItem() throws NotFoundException {
        long itemId = 0L;
        long userId = 0L;
        Item expectedItem = new Item();
        User user = new User();
        user.setId(1L);
        expectedItem.setOwner(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        Item actualItem = itemService.getById(itemId);

        assertThat(expectedItem, equalTo(actualItem));
    }

    @Test
    void getItemByIdWhenItemNotFoundThenExceptionThrown() {
        long itemId = 0L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getById(itemId));

        assertThat("не найдена вещь с id = 0", equalTo(exception.getMessage()));
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void saveItemWhenItemValidThenSavedItem() throws NotFoundException {
        Item itemToSave = new Item();
        itemToSave.setAvailable(true);
        User user = new User();
        when(itemRepository.save(any(Item.class)))
                .thenReturn(itemToSave);
        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.of(new ItemRequest()));

        Item actualItem = itemService.createItem(
                new CreateItemRequest(itemToSave.getName(), itemToSave.getDescription(), itemToSave.isAvailable(), 1L), user.getId());

        assertThat(itemToSave, equalTo(actualItem));
    }

    @Test
    void updateItemExceptionTest() {
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

    @Test
    void findItemsWhenInvokedWithEmptyTextThenReturnedList() throws NotFoundException {
        Long userId = 0L;
        when(itemRepository.findAllByOwnerWithComments(any()))
                .thenReturn(List.of(new Item()));

        assertDoesNotThrow(() -> itemService.getItemsByUserId(userId));
        verify(itemRepository, never()).search("");
    }

    @Test
    void findItemsWhenInvokedThenReturnedEmptyList() throws NotFoundException {
        Long userId = 0L;

        List<Item> actualItems = itemService.getItemsByUserId(userId);

        assertThat(actualItems, empty());
    }

    @Test
    void deleteTest() throws NotFoundException {
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