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
//                userService.getById(newUser.getId()).getId(), user1.getId());
    }

//    @Test
//    void updateUserTest() throws NotFoundException, ConflictException {
////        UserDto userDto = userService.getById(1L);
////        userDto.setName("test");
////        userDto.setEmail("test@test.ru");
//        User newUser = new User();
//        when(userRepository.getById(anyLong()))
//                        .thenReturn(newUser);
//        when(userService.getById(anyLong()))
//                .thenReturn(newUser);
//        User changedUser = new User(newUser.getId(), "changed_name", "changed_email");
//        when(userRepository.save(any()))
//                .thenReturn(changedUser);
////        userService.createUser(userMapper.toRequest(user1));
//        User answer = userService.updateUser(1L, new UpdateUserRequest("test", "email"));
//        assertEquals("changed_name", answer.getName());
//        assertEquals("changed_email", answer.getEmail());
////        assertEquals("test@test.ru", userService.getById(1L).getEmail());
//    }

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
//        userService.createUser(request);  // UserMapper.toUserDto(user2));
        final ConflictException exception = assertThrows(ConflictException.class, () -> userService.createUser(request));
        assertEquals("пользователь с почтой user1@mail.ru уже зарегистрирован", exception.getMessage());
    }

//    @Test
//    void createUserWithNullEmailTest() {
////        User user4 = new User(4, "user4", null);
//        final ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(new CreateUserRequest(null, null)));
//        assertEquals("Email not found", exception.getMessage());
//    }

    @Test
    void getUserNotByIdTest() {
        final NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(123123L));
        assertEquals("Не найден пользователь с userId = 123123", exception.getMessage());
    }
}