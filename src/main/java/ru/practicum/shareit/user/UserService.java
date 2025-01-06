package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    public User createUser(@Valid CreateUserRequest request) {
        log.info("creating user {}", request);
        // todo
    }

    public User getUserById(Long id) {
        // todo
    }

    public User updateUser(Long userId, UpdateUserRequest request) {
        // todo
    }

    public void deleteUser(Long userId) {
        // todo
    }
}
