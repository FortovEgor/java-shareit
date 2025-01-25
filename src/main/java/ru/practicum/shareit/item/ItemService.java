package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class ItemService {

    private final UserService userService;
    private final ItemRepository repo;
    private final ItemMapper itemMapper;

    public Item createItem(@Valid CreateItemRequest request, Long userId) throws NotFoundException {
        User owner = userService.getUserById(userId);
        Item item = itemMapper.toItem(request);
        item.setOwner(owner);
        return repo.save(item);
    }

    public Item updateItem(@Valid UpdateItemRequest request, Long itemId, Long userId) throws ForbiddenException, NotFoundException {
        Item item = getItemById(itemId);
        User owner = userService.getUserById(userId);

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

    public Item getItemById(Long itemId) throws NotFoundException {
        return repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("не найдена вещь с id = %s", itemId));
    }

    public List<Item> getItemsByUserId(Long userId) {
        return repo.findAllByOwnerId(userId);
    }

    public List<Item> search(String searchString) {
        if (searchString == null || searchString.isBlank()) {
            return List.of();
        }
        return repo.search(searchString);
    }
}
