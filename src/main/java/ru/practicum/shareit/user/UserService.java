package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepo;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor  // for DI
public class UserService {
    private final UserRepo repo;
    private final UserMapper userMapper;

    public User createUser(@Valid CreateUserRequest request) {
        log.info("Creating user {}", request);
        checkEmail(request.getEmail());
        User user = userMapper.toUser(request);
        log.info("Saving user {}", user);
        return repo.save(user);
    }

    private void checkEmail(String email) {
        if (repo.existsByEmail(email)) {
            throw new ConflictException("пользователь с почтой %s уже зарегистрирован", email);
        }
    }

    public User getUserById(Long userId) {
        return repo.getById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с userId = %d", userId));
    }

    public User updateUser(Long userId, UpdateUserRequest request) {
        log.info("Updating user with id = {} with {}", userId, request);
        User user = getUserById(userId);
        log.info("User with id = {} found");

        String newName = request.getName();
        String newEmail = request.getEmail();

        checkEmailUniqueness(userId, newEmail);

        if (newName != null) {
            user.setName(newName);
        }

        if (newEmail != null) {
            user.setEmail(newEmail);
        }

        return repo.save(user);
    }

    public void deleteUser(Long userId) {
        repo.deleteById(userId);
    }

    private void checkEmailUniqueness(long userId, String email) {
        repo.getByEmail(email)
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(userId)) {
                        throw new ConflictException("Пользователь с почтой %s уже зарегистрирован", email);
                    }
                });
    }
}
