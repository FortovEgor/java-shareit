package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<ItemDto> createItem(@RequestBody CreateItemRequest request,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemService.createItem(request, userId);
        return new ResponseEntity<>(itemMapper.toDto(item), HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody UpdateItemRequest request,
                                              @PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemService.updateItem(request, itemId, userId);
        return ResponseEntity.ok().body(itemMapper.toDto(item));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        // todo
        Item item = itemService.getItemById(itemId);
        return ResponseEntity.ok().body(itemMapper.toDto(item));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        // todo
        List<Item> items = itemService.getItemsByUserId(userId);
        return ResponseEntity.ok().body(itemMapper.toDto(items));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam("text") String searchString) {
        List<Item> items = itemService.search(searchString);
        return ResponseEntity.ok().body(itemMapper.toDto(items));
    }
}
