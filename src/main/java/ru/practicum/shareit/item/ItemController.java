package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemMapper itemMapper;
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody CreateItemRequest request,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        Item item = itemService.createItem(request, userId);
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
        Item item = itemService.getItemById(itemId);
        return itemMapper.toDto(item);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<Item> items = itemService.getItemsByUserId(userId);
        return itemMapper.toDto(items);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String searchString) {
        List<Item> items = itemService.search(searchString);
        return itemMapper.toDto(items);
    }
}
