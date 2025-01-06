package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User userBody) {
        return userStorage.createUser(userBody);
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @DeleteMapping()
    public ResponseEntity<User> deleteUser(@RequestBody User user) {
        return userStorage.deleteUser(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return userStorage.getUsers();
    }

}
