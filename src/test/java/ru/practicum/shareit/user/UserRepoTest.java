package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dao.UserRepo;
import ru.practicum.shareit.user.dao.UserRepoImpl;
import ru.practicum.shareit.util.TestUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class UserRepoTest {

    private UserRepo userRepo;

    @BeforeEach
    void setup() {
        userRepo = new UserRepoImpl();
        log.info("Running test");
    }

    @Test
    void givenUserWhenSavedGotSaved() {
        User user = createAndSaveUser();
        User actUser = getUserById(user.getId());

        assertNotNull(actUser.getId());
        assertEquals(user.getId(), actUser.getId());
        assertEquals(user.getName(), actUser.getName());
        assertEquals(user.getEmail(), actUser.getEmail());
    }

    @Test
    void givenUserWhenGetByEmailGotUser() {
        User user = createAndSaveUser();

        User actUser = getUserByEmail(user.getEmail());

        assertEquals(user.getId(), actUser.getId());
    }

    @Test
    void givenItemWhenDeleteGotDeleted() {
        User user = createAndSaveUser();

        userRepo.deleteById(user.getId());

        Optional<User> actUser = userRepo.getById(user.getId());

        assertTrue(actUser.isEmpty());
    }

    private User createAndSaveUser() {
        User user = TestUtil.getUser();
        return userRepo.save(user);
    }

    private User getUserById(long userId) {
        return userRepo.getById(userId).orElseThrow();
    }

    private User getUserByEmail(String email) {
        return userRepo.getByEmail(email).orElseThrow();
    }
}