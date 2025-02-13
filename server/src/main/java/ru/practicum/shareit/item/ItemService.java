package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemLastNextBookDate;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository repo;
    private final ItemMapper itemMapper;

    @Transactional
    public Item createItem(@Valid CreateItemRequest request, Long userId) throws NotFoundException {
        User owner = userService.getById(userId);
        Item item = itemMapper.toItem(request);
        item.setOwner(owner);
        Long requestId = request.getRequestId();
        if (requestId != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Could not find item request with id = " + requestId));
            item.setRequest(itemRequest);
        }
        return repo.save(item);
    }

    @Transactional
    public Item updateItem(@Valid UpdateItemRequest request, Long itemId, Long userId) throws ForbiddenException, NotFoundException {
        Item item = getById(itemId);
        User owner = userService.getById(userId);

        // проверка что только автор может ее менять
        if (!item.getOwner().getId().equals(owner.getId())) {
            throw new ForbiddenException("Пользователю %s запрещено изменять вещь %s, он не ее владелец", userId, itemId);
        }

        String newName = request.getName();
        String newDescription = request.getDescription();
        Boolean newAvailable = request.getAvailable();

        if (newName != null) {
            item.setName(newName);
        }

        if (newDescription != null) {
            item.setDescription(newDescription);
        }

        if (newAvailable != null) {
            item.setAvailable(newAvailable);
        }

        return repo.save(item);
    }

    public Item getById(Long itemId) throws NotFoundException {
        return repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("не найдена вещь с id = %s", itemId));
    }

    public List<Item> getItemsByUserId(Long userId) throws NotFoundException {
        LocalDateTime now = LocalDateTime.now();
        User owner = userService.getById(userId);
        Map<Long, ItemLastNextBookDate> itemById = groupById(repo.getLastAndNextBookingDate(owner, now));

        List<Item> items = repo.findAllByOwnerWithComments(owner);
        items.forEach(item -> {
            if (itemById.get(item.getId()) != null) {
                ItemLastNextBookDate date = itemById.get(item.getId());
                item.setLastBooking(date.getLastBooking());
                item.setNextBooking(date.getNextBooking());
            }
        });

        return items;
    }

    @Transactional
    public void deleteById(long itemId, long userId) throws NotFoundException, ForbiddenException {

        Item item = getById(itemId);
        User owner = userService.getById(userId);
        if (!item.getOwner().getId().equals(owner.getId())) {
            throw new ForbiddenException("пользователю %s запрещено удалять вещь %s", userId, itemId);
        }

        repo.deleteById(itemId);
    }

    public List<Item> search(String searchString) {
        if (searchString == null || searchString.isBlank()) {
            return List.of();
        }
        return repo.search(searchString);
    }

    private Map<Long, ItemLastNextBookDate> groupById(List<ItemLastNextBookDate> items) {
        Map<Long, ItemLastNextBookDate> itemById = new HashMap<>();
        items.forEach(item -> itemById.put(item.getId(), item));
        return itemById;
    }
}
