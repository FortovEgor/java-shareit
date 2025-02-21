package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemMapper itemMapper;
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ItemDto createItem(@RequestBody CreateItemRequest request,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        Item item = itemService.createItem(request, userId);
        log.info("BEBEBE: " + request.toString());
        return itemMapper.toDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody UpdateItemRequest request,
                                              @PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) throws ForbiddenException, NotFoundException {
        Item item = itemService.updateItem(request, itemId, userId);
        return itemMapper.toDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) throws NotFoundException {
        Item item = itemService.getById(itemId);
        return itemMapper.toDto(item);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        List<Item> items = itemService.getItemsByUserId(userId);
        return itemMapper.toDto(items);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String searchString) {
        List<Item> items = itemService.search(searchString);
        return itemMapper.toDto(items);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestBody CreateCommentRequest request,
                                                    @PathVariable Long itemId,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException, BadRequest {

//        return new ResponseEntity<>(itemMapper.toCommentDto(new Comment()), HttpStatus.CREATED);
        Comment comment = commentService.createComment(request, itemId, userId);
        return new ResponseEntity<>(itemMapper.toCommentDto(comment), HttpStatus.CREATED);
    }
}
