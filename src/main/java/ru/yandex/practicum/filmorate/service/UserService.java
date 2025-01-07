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

    public ResponseEntity<List<User>> getUsers() {
        return userStorage.getUsers();
    }
}
