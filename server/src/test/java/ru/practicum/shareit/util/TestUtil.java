package ru.practicum.shareit.util;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Random;

public class TestUtil {

    private static final Random random = new Random();

    public static User getUser(long id) {
        User user = getUser();
        user.setId(id);
        return user;
    }

    public static User getUser() {
        String userName = getRandomString(7);
        return User.builder()
                .name(userName)
                .email(userName + "@yandex.ru")
                .build();
    }

    public static Item getItem(User owner) {
        Item item = getItem();
        item.setOwner(owner);
        return item;
    }

    public static Item getItem() {
        return Item.builder()
                .name(getRandomString(7))
                .description(getRandomString(10))
                .available(randomBoolean())
                .build();
    }

    public static String getRandomString(int targetStringLength) {
        int leftLimit = 97;  // letter 'a'
        int rightLimit = 122;  // letter 'z'

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }
}