package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.Optional;

@Repository
public class UserRepoImpl implements UserRepo {

    @Override
    public User save(User user) {
        // @Todo
        return null;
    }

    @Override
    public Optional<User> getById(long userId) {
        // @Todo
        return Optional.empty();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        // @Todo
        return Optional.empty();
    }

    @Override
    public void deleteById(long userId) {
        // @Todo
    }
}
