package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.exceptions.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.repository.user.InMemoryUserRepo;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User testUser;
    private User secondTestUser;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserRepo());
        userController = new UserController(userService);
        testUser = new User(
                1,
                "test@email.com",
                "testLogin",
                "Test Name",
                LocalDate.of(1990, 1, 1)
        );

        secondTestUser = new User(
                2,
                "test@email.com",
                "testLogin",
                "Second name",
                LocalDate.of(1991, 1, 1)
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
        userController.createUser(secondTestUser);

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

    @Test
    void addFriend_ShouldReturnOkStatus() {
        userController.createUser(testUser);
        userController.createUser(secondTestUser);

        ResponseEntity<User> response = userController.addFriend(1L, 2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addFriend_WithSameIds_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> userController.addFriend(1L, 1L));
    }

    @Test
    void addFriend_WithNonExistingUser_ShouldThrowException() {
        assertThrows(UserNotFoundException.class, () -> userController.addFriend(1L, 2L));
    }

    @Test
    void getFriends_ShouldReturnCommonFriends() {
        userController.createUser(testUser);
        userController.createUser(secondTestUser);

        User userWithNullName = new User(
                3,
                "test@email.com",
                "testLogin",
                "someName",
                LocalDate.of(1992, 1, 1)
        );

        userController.createUser(userWithNullName);
        // add friend to both of them
        userController.addFriend(1L, 3L);
        userController.addFriend(2L, 3L);

        ResponseEntity<List<User>> response = userController.getCommonFriends(1L, 2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(3L, response.getBody().get(0).getId());
    }

    @Test
    void removeFriend_ShouldReturnOkStatus() {
        userController.createUser(testUser);
        userController.createUser(secondTestUser);

        userController.addFriend(1L, 2L);

        ResponseEntity<User> response = userController.removeFriend(1L, 2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void friendGet_UnknownID_ShouldReturnNotFound() {
        userController.createUser(testUser);
        ResponseEntity<User> response = userController.addFriend(1L, 999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}