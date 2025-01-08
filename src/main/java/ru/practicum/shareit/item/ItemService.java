package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepo;
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
    private final ItemRepo repo;
    private final ItemMapper itemMapper;

    public Item createItem(@Valid CreateItemRequest request, Long userId) {
        User owner = userService.getUserById(userId);
        Item item = itemMapper.toItem(request);
        item.setOwner(owner);
        return repo.save(item);
    }

    public Item updateItem(@Valid UpdateItemRequest request, Long itemId, Long userId) {
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

    public Item getItemById(Long itemId) {
        return repo.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("не найдена вещь с id = %s", itemId));
    }

    public List<Item> getItemsByUserId(Long userId) {
        return repo.getByUserId(userId);
    }

    public List<Item> search(String searchString) {
        return repo.search(searchString);
    }
}
