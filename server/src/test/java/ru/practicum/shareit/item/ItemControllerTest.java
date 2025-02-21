package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @Mock
    private CommentService commentService;
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private ItemController itemController;

    private final UserDto userDto1 = new UserDto(1, "user1", "user1@user1.ru");
    private final ItemDto itemDto1 = new ItemDto(1L, "item1",
            "description1", true, null, LocalDateTime.now(), LocalDateTime.now());

    private final ItemDto itemDtoWithBooking = new ItemDto(2L, "itemDtoWithBooking", "descriptionDtoWithBooking",
            true, null, null, null);
    private final CommentDto commentDto1 = new CommentDto(1L, "comment1", "user1", Instant.now());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
    }

    @Test
    void createItemTest() throws Exception {
        Item item = new Item(itemDto1.getId(), new User(), itemDto1.getName(), itemDto1.getDescription(), itemDto1.isAvailable(), null, new ItemRequest());
        when(itemService.createItem(any(), any()))
                .thenReturn(item);
        mockMvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto1))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto1.isAvailable()), Boolean.class));
    }

    @Test
    void updateItemTest() throws Exception {
        Item item = new Item(itemDto1.getId(), new User(), itemDto1.getName(), itemDto1.getDescription(),
                itemDto1.isAvailable(), null, new ItemRequest());
        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(item);
        mockMvc.perform(patch("/items/" + itemDto1.getId())
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto1.isAvailable()), Boolean.class));
    }

    @Test
    void getItemByIdTest() throws Exception {
        Item item = new Item(itemDtoWithBooking.getId(), new User(), itemDtoWithBooking.getName(), itemDtoWithBooking.getDescription(),
                itemDtoWithBooking.isAvailable(), null, new ItemRequest());
        when(itemService.getById(anyLong()))
                .thenReturn(item);
        mockMvc.perform(get("/items/" + itemDto1.getId())
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBooking.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDtoWithBooking.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoWithBooking.isAvailable()), Boolean.class));
    }

    @Test
    void searchByTextTest() throws Exception {
        Item item = new Item(itemDto1.getId(), new User(), itemDto1.getName(), itemDto1.getDescription(),
                itemDto1.isAvailable(), null, new ItemRequest());
        when(itemService.search(anyString()))
                .thenReturn(List.of(item));
        mockMvc.perform(get("/items/search").param("text", "item1")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName()), String.class));
    }

    @Test
    void addIncorrectCommentTest() throws Exception {
        Comment comment = new Comment(commentDto1.getId(), commentDto1.getText(),
                new User(100L, commentDto1.getAuthorName(), "email"), new Item(), Instant.now());
        mockMvc.perform(post("/items/{itemId}/comment/", itemDto1.getId())
                        .content(mapper.writeValueAsString(new CreateCommentRequest("text")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void createCommentNotFoundTest() throws Exception {
        Item item = new Item(itemDto1.getId(), new User(), itemDto1.getName(), itemDto1.getDescription(), itemDto1.isAvailable(), null, new ItemRequest());

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto1))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getItemsByUserIdTest() throws Exception {
        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto1))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().is2xxSuccessful());
    }
}