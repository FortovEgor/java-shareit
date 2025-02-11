package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequestItem(@RequestBody ItemRequestDto request,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        ItemRequest itemRequest = itemRequestService.createItemRequest(request, userId);
        return ItemRequestMapper.INSTANCE.toDto(itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        List<ItemRequest> itemRequests = itemRequestService.getUserRequests(userId);
        return ItemRequestMapper.INSTANCE.toDto(itemRequests);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        List<ItemRequest> itemRequests = itemRequestService.getAllOtherUsersRequests(userId);
        return ItemRequestMapper.INSTANCE.toDto(itemRequests);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        ItemRequest itemRequest = itemRequestService.getRequestById(requestId, userId);
        return ItemRequestMapper.INSTANCE.toDto(itemRequest);
    }
}
