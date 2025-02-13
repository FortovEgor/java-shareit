package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Mock
    private BookingService bookingService;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createCommentTest() throws NotFoundException {
        User user = new User(1L, "name", "email");
        Item item = new Item(1L, user, "a", "b", true, null, null);
        when(itemService.getById(any()))
                .thenReturn(item);
        when(userService.getById(anyLong()))
                .thenReturn(user);
        when(bookingService.existPastApprovedItemBookingByUser(any(), any()))
                .thenReturn(true);
        when(commentRepository.save(any()))
                .thenReturn(new Comment());

        CreateCommentRequest request = new CreateCommentRequest("some text");
        assertDoesNotThrow(() -> commentService.createComment(request, item.getId(), user.getId()));
    }

}
