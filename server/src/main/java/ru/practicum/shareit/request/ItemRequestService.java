package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Transactional
    public ItemRequest createItemRequest(ItemRequestDto request, Long userId) throws NotFoundException {
        User user = userService.getById(userId);
        log.info("User " + user + " is creating a new item request");
        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.toItemRequest(request, user);
        itemRequest.setRequestor(user);
        return itemRequestRepository.save(itemRequest);
    }

    public List<ItemRequest> getUserRequests(Long userId) throws NotFoundException {
        User user = userService.getById(userId);
        log.info("User " + user + " is asking for all his requests");
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
    }

    public List<ItemRequest> getAllOtherUsersRequests(Long userId) throws NotFoundException {
        User user = userService.getById(userId);
        log.info("User " + user + " is asking for all other requests");
        return itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
    }

    public ItemRequest getRequestById(Long itemRequestId, Long userId) throws NotFoundException {
        User user = userService.getById(userId);
        log.info("User " + user + " is asking for item request with id = " + itemRequestId);
        return itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос с itemRequestId = %d", itemRequestId));
    }
}
