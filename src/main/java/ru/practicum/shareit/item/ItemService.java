package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ItemService {
    public Item createItem(CreateItemRequest request, Long userId) {
        // todo
        return Item.builder().build();
    }

    public Item updateItem(UpdateItemRequest request, Long itemId, Long userid) {
        // todo
        return Item.builder().build();
    }

    public Item getItemById(Long itemId) {
        // todo
        return Item.builder().build();
    }

    public List<Item> getItemsByUserId(Long userId) {
        // todo
        return new ArrayList<>();
    }

    public List<Item> search(String searchString) {
        // todo
        return new ArrayList<>();
    }
}
