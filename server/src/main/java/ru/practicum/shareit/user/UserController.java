package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody CreateUserRequest request) throws ConflictException {
        User user = userService.createUser(request);
        return userMapper.toDto(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) throws NotFoundException {
        User user = userService.getById(userId);
        return userMapper.toDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest request) throws ConflictException, NotFoundException {
        User user = userService.updateUser(userId, request);
        return userMapper.toDto(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
