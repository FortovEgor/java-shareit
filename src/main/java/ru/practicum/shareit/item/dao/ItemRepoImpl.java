package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepoImpl implements ItemRepo {
    private Map<Long, Item> items = new HashMap<>();
    private long counter = 0;  // Invariant: only increases; needed for generating item id

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            long id = generateId();
            item.setId(id);
            items.put(id, item);
        } else {
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Item> search(String searchString) {
        return items.values().stream()
                .filter(item -> {
                    if (searchString.trim().isEmpty() || !item.isAvailable()) {
                        return false;
                    }
                    String lowerSubstring = searchString.toLowerCase();
                    return item.getName().toLowerCase().contains(lowerSubstring) ||
                            item.getDescription().toLowerCase().contains(lowerSubstring);
                })
                .toList();
    }

    @Override
    public void deleteById(Long itemId) {
        items.remove(itemId);
    }

    private long generateId() {
        return counter++;
    }
}
