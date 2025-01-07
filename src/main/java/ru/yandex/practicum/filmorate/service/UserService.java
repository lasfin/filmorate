package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public ResponseEntity<User> createUser(@Valid @RequestBody User userBody) {
        return userStorage.createUser(userBody);
    }

    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return userStorage.updateUser(user);
    }

    public ResponseEntity<User> deleteUser(@RequestBody User user) {
        return userStorage.deleteUser(user);
    }

    public ResponseEntity<User> getUser(Long userId) {
        return userStorage.getUser(userId);
    }

    public ResponseEntity<List<User>> getUsers() {
        return userStorage.getUsers();
    }

    public ResponseEntity<User> addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new RuntimeException("User cannot be a friend of themselves");
        }

        final User user = userStorage.getUser(userId).getBody();
        final User friend = userStorage.getUser(friendId).getBody();

        if (user == null || friend == null) {
            // not found exception?
            throw new RuntimeException("User or friend not found");
        }

        return userStorage.addFriend(userId, friendId);
    }

    public ResponseEntity<User> removeFriend(Long userId, Long friendId) {
        final User user = userStorage.getUser(userId).getBody();
        final User friend = userStorage.getUser(friendId).getBody();

        if (user == null || friend == null) {
            // not found exception?
            throw new RuntimeException("User or friend not found");
        }

        return userStorage.removeFriend(userId, friendId);
    }
}
