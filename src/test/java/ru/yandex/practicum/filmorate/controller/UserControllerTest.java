package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User testUser;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userController = new UserController(userStorage);
        testUser = new User(
                1,
                "test@email.com",
                "testLogin",
                "Test Name",
                LocalDate.of(1990, 1, 1)
        );
    }

    @Test
    void createUser_ShouldReturnCreatedStatus() {
        ResponseEntity<User> response = userController.createUser(testUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(testUser.getEmail(), response.getBody().getEmail());
        assertEquals(testUser.getLogin(), response.getBody().getLogin());
        assertEquals(testUser.getName(), response.getBody().getName());
        assertEquals(testUser.getBirthday(), response.getBody().getBirthday());
    }

    @Test
    void createUser_ShouldIncrementId() {
        ResponseEntity<User> response1 = userController.createUser(testUser);
        ResponseEntity<User> response2 = userController.createUser(testUser);

        assertEquals(1L, response1.getBody().getId());
        assertEquals(2L, response2.getBody().getId());
    }

    @Test
    void updateUser_WithExistingId_ShouldUpdateAndReturnOkStatus() {
        // First create a user
        ResponseEntity<User> createResponse = userController.createUser(testUser);
        Long userId = createResponse.getBody().getId();

        // Prepare updated user data
        User updatedUser = new User(
                userId,
                "updated@email.com",
                "updatedLogin",
                "Updated Name",
                LocalDate.of(1991, 1, 1)
        );

        ResponseEntity<User> updateResponse = userController.updateUser(updatedUser);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals(updatedUser.getEmail(), updateResponse.getBody().getEmail());
        assertEquals(updatedUser.getLogin(), updateResponse.getBody().getLogin());
        assertEquals(updatedUser.getName(), updateResponse.getBody().getName());
        assertEquals(updatedUser.getBirthday(), updateResponse.getBody().getBirthday());
    }

    @Test
    void updateUser_WithNonExistingId_ShouldThrowException() {
        User nonExistingUser = new User(
                999L,
                "test@email.com",
                "testLogin",
                "Test Name",
                LocalDate.of(1990, 1, 1)
        );

        assertThrows(RuntimeException.class, () -> userController.updateUser(nonExistingUser));
    }

    @Test
    void deleteUser_ShouldReturnNoContentStatus() {
        ResponseEntity<User> createResponse = userController.createUser(testUser);
        User createdUser = createResponse.getBody();

        ResponseEntity<User> deleteResponse = userController.deleteUser(createdUser);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<List<User>> getResponse = userController.getUsers();
        assertTrue(getResponse.getBody().isEmpty());
    }

    @Test
    void getUsers_WithNoUsers_ShouldReturnEmptyList() {
        ResponseEntity<List<User>> response = userController.getUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getUsers_WithMultipleUsers_ShouldReturnAllUsers() {
        // Create multiple users
        userController.createUser(testUser);
        userController.createUser(new User(
                1,
                "test2@email.com",
                "testLogin2",
                "Test Name 2",
                LocalDate.of(1992, 1, 1)
        ));

        ResponseEntity<List<User>> response = userController.getUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void createUser_WithNullValues_ShouldCreateUserWithProvidedValues() {
        User userWithNullName = new User(
                1,
                "test@email.com",
                "testLogin",
                null,
                LocalDate.of(1990, 1, 1)
        );

        ResponseEntity<User> response = userController.createUser(userWithNullName);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testLogin", response.getBody().getName());  // Name should default to login if null
    }
}