package ru.practicum.shareit.item;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {

    ItemDto toDto(Item item);

    List<ItemDto> toDto(List<Item> item);

    Item toItem(CreateItemRequest request);
}
