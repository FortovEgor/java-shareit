package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepo {
    Item save(Item item);

    Optional<Item> getItemById(Long itemId);

    List<Item> getByUserId(Long userId);

    List<Item> search(String searchString);

    void deleteById(Long itemId);
}
