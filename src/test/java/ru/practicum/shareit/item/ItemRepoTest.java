package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dao.ItemRepo;
import ru.practicum.shareit.item.dao.ItemRepoImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepo;
import ru.practicum.shareit.user.dao.UserRepoImpl;
import ru.practicum.shareit.util.TestUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ItemRepoTest {

    private ItemRepo itemRepo;
    private UserRepo userRepo;

    @BeforeEach
    void setup() {
        itemRepo = new ItemRepoImpl();
        userRepo = new UserRepoImpl();
    }

    @Test
    public void givenItemWhenSavedGotSaved() {
        User owner = createAndSaveUser();
        Item item = createAndSaveItem(owner);

        Item actItem = getItemById(item.getId());

        assertEquals(owner.getId(), actItem.getOwner().getId());
        assertEquals(item.getName(), actItem.getName());
        assertEquals(item.getDescription(), actItem.getDescription());
        assertEquals(item.isAvailable(), actItem.isAvailable());
    }

    @Test
    public void givenItemWithOwnerWhenGetByOwnerIdGotItemList() {
        User owner1 = createAndSaveUser();
        User owner2 = createAndSaveUser();
        Item item1 = createAndSaveItem(owner1);
        Item item2 = createAndSaveItem(owner1);
        Item item3 = createAndSaveItem(owner1);
        Item item4 = createAndSaveItem(owner2);
        Item item5 = createAndSaveItem(owner2);

        Set<Long> items1 = getItemIdSet(itemRepo.getByUserId(owner1.getId()));
        Set<Long> expItems1 = Set.of(item1.getId(), item2.getId(), item3.getId());

        assertEquals(expItems1, items1);

        Set<Long> items2 = getItemIdSet(itemRepo.getByUserId(owner2.getId()));
        Set<Long> expItems2 = Set.of(item4.getId(), item5.getId());

        assertEquals(expItems2, items2);
    }

    @Test
    public void givenItemWhenDeleteGotDeleted() {
        User owner = createAndSaveUser();
        Item item1 = createAndSaveItem(owner);
        Item item2 = createAndSaveItem(owner);

        itemRepo.deleteById(item1.getId());

        List<Item> items = itemRepo.getByUserId(owner.getId());

        assertEquals(1, items.size());
        assertEquals(item2.getId(), items.getFirst().getId());
    }

    @Test
    public void givenAvailableItemWhenSearchGotItemList() {
        User owner = createAndSaveUser();
        Item item1 = createAndSaveItem(owner);
        Item item2 = createAndSaveItem(owner);

        item1.setAvailable(true);
        item2.setAvailable(true);

        // у первой вещи будет подходящее имя
        // у второй вещи будет подходящее описание
        String string = item1.getName();
        item1.setName(string.toLowerCase());
        item2.setDescription(TestUtil.getRandomString(5) + string +
                TestUtil.getRandomString(5));

        String searchString = string.toUpperCase();

        List<Item> items = itemRepo.search(searchString);

        assertEquals(2, items.size());
    }

    @Test
    public void givenUnAvailableItemWhenSearchGotEmptyList() {
        User owner = createAndSaveUser();
        Item item1 = createAndSaveItem(owner);
        Item item2 = createAndSaveItem(owner);

        item1.setAvailable(false);
        item2.setAvailable(false);

        // у первой вещи будет подходящее имя
        // у второй вещи будет подходящее описание
        String string = item1.getName();
        item1.setName(string.toLowerCase());
        item2.setDescription(TestUtil.getRandomString(5) + string +
                TestUtil.getRandomString(5));

        String searchString = string.toUpperCase();

        List<Item> items = itemRepo.search(searchString);

        assertEquals(0, items.size());
    }

    @Test
    public void givenItemWhenSearchEmptyStringGotEmptyList() {
        User owner = createAndSaveUser();
        Item item1 = createAndSaveItem(owner);
        Item item2 = createAndSaveItem(owner);

        item1.setAvailable(true);
        item2.setAvailable(true);

        List<Item> items = itemRepo.search("");

        assertEquals(0, items.size());
    }

    private User createAndSaveUser() {
        User user = TestUtil.getUser();
        return userRepo.save(user);
    }

    private Item createAndSaveItem(User owner) {
        Item item = TestUtil.getItem(owner);
        return itemRepo.save(item);
    }

    private Item getItemById(long itemId) {
        return itemRepo.getItemById(itemId).orElseThrow();
    }

    private Set<Long> getItemIdSet(List<Item> items) {
        return items.stream()
                .map(Item::getId)
                .collect(Collectors.toSet());
    }
}