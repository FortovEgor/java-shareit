package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.item.model.Item;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;

    @Transactional
    public Comment createComment(@Valid CreateCommentRequest request, Long itemId, Long userId) throws NotFoundException, BadRequest {

        Item item = itemService.getById(itemId);
        User user = userService.getById(userId);

        log.info("item = {}", item);
        log.info("user = {}", user);

        if (!bookingService.existPastApprovedItemBookingByUser(item, user)) {
            throw new BadRequest("запрещено оставлять комментарий для вещи %s пользователем %s",
                    itemId, userId);
        }

        Comment comment = itemMapper.toComment(request);
        comment.setItem(item);
        comment.setAuthor(user);

        item.getComments().add(comment);

        return commentRepository.save(comment);
    }
}