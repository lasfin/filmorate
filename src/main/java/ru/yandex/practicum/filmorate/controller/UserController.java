package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User userBody) {
        return userService.createUser(userBody);
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping()
    public ResponseEntity<User> deleteUser(@RequestBody User user) {
        return userService.deleteUser(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return userService.getUsers();
    }

}
