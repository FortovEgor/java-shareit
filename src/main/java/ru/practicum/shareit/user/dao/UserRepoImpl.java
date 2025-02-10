package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Deprecated
@Repository
public class UserRepoImpl implements UserRepo {
    private final Map<Long, User> users = new HashMap<>();
    private long counter = 0;  // invariant: ONLY increases; need it for user id


    @Override
    public User save(User user) {
        if (user.getId() == null) {
            long id = getNextId();
            user.setId(id);
            users.put(id, user);
        } else {
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public Optional<User> getById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user ->
                    user.getEmail().equals(email));
    }

    @Override
    public void deleteById(long userId) {
        users.remove(userId);
    }

    private long getNextId() {
        return counter++;
    }
}
