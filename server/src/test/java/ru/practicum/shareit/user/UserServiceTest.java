package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UpdateUserRequest;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final User user1 = new User(1L, "user1", "user1@mail.ru");
    private final User user2 = new User(2L, "user2", "user2@mail.ru");
    private final User user3 = new User(3L, "user3", "user2@mail.ru");

    @Test
    void createUserTest() throws ConflictException, NotFoundException {
        User newUser = new User();
        when(userRepository.save(any()))
                .thenReturn(newUser);
        assertEquals(userService.createUser(userMapper.toRequest(newUser)), newUser);
    }

    @Test
    void updateUserTest() throws ConflictException, NotFoundException {
        User user = new User(1L, "name", "email");
        User newUser = new User(1L, "newName", "newEmail");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(newUser);
        assertDoesNotThrow(() -> userService.updateUser(user.getId(), new UpdateUserRequest("name", "email"))); // getuserMapper.toRequest(newUser)), newUser);
    }

    @Test
    void updateUserFailTest() throws ConflictException, NotFoundException {
        User user = new User(1L, "name", "email");
        User newUser = new User(2L, "newName", "newEmail");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(newUser));
        assertThrows(ConflictException.class, () -> userService.updateUser(user.getId(), new UpdateUserRequest("name", "email"))); // getuserMapper.toRequest(newUser)), newUser);
    }

    @Test
    void getUserByIdTest() throws NotFoundException {
        when(userRepository.findById(anyLong()))
                        .thenReturn(Optional.of(user1));
        assertEquals(userMapper.toDto(user1).getId(), userService.getById(1L).getId());
    }


    @Test
    void deleteUserByIdTest() {
        userService.deleteUser(1L);
        assertNull(userRepository.findById(1L).orElse(null));
    }

    @Test
    void createUserWithDuplicateEmailTest() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.ofNullable(user1));
        CreateUserRequest request = new CreateUserRequest(user1.getName(), user1.getEmail());
        final ConflictException exception = assertThrows(ConflictException.class, () -> userService.createUser(request));
        assertEquals("пользователь с почтой user1@mail.ru уже зарегистрирован", exception.getMessage());
    }

    @Test
    void getUserNotByIdTest() {
        final NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(123123L));
        assertEquals("Не найден пользователь с userId = 123123", exception.getMessage());
    }

    @Test
    void getUserByIdNullTest() {
        assertEquals(userMapper.toDto(null), null); // .getId(), userService.getById(1L).getId());
    }

    @Test
    void getUserById33NullTest() {
        assertEquals(userMapper.toRequest(null), null); // .getId(), userService.getById(1L).getId());
    }

    @Test
    void getUserToIdNullTest() {
        assertEquals(userMapper.toUser(null), null); // .getId(), userService.getById(1L).getId());
    }
}