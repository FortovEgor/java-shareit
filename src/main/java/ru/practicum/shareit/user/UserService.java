package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor  // for DI
public class UserService {
    private final UserRepository repo;
    private final UserMapper userMapper;

    public User createUser(@Valid CreateUserRequest request) throws ConflictException {
        log.info("Creating user {}", request);
        checkEmail(request.getEmail());
        User user = userMapper.toUser(request);
        log.info("Saving user {}", user);
        return repo.save(user);
    }

    private void checkEmail(String email) throws ConflictException {
        Optional<User> existingUser = repo.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new ConflictException("пользователь с почтой %s уже зарегистрирован", email);
        }
    }

    public User getUserById(Long userId) throws NotFoundException {
        return repo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с userId = %d", userId));
    }

    public User updateUser(Long userId, UpdateUserRequest request) throws ConflictException, NotFoundException {
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

    private void checkEmailUniqueness(long userId, String email) throws ConflictException {
        AtomicBoolean throwException = new AtomicBoolean(false);
        repo.findByEmail(email)
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(userId)) {
                        throwException.set(true);
                    }
                });
        if (throwException.get()) {
            throw new ConflictException("Пользователь с почтой %s уже зарегистрирован", email);
        }
    }
}
