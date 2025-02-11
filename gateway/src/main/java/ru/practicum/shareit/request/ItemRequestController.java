package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен список запросов текущего пользователя вместе с данными об ответах " +
                "на них с id = {}.", userId);
        return itemRequestClient.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен список запросов пользователя с id = {}, созданных другими", userId);
        return itemRequestClient.getAllItemRequestsByOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        log.info("Получен запрос с id = {}, userId={}.", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Добавлен новый запрос на бронирование: {}", itemRequestDto);
        return itemRequestClient.saveItemRequest(userId, itemRequestDto);
    }

}