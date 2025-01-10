package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.Optional;

public interface UserRepo {
    User save(User user);

    Optional<User> getById(long userId);

    Optional<User> getByEmail(String email);

    boolean existsByEmail(String email);

    void deleteById(long userId);
}
