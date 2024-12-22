package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private Long nextId = 1L;
    private ArrayList<User> users = new ArrayList<>();

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User userBody) {
        User newUser = new User(
                nextId++,
                userBody.getEmail(),
                userBody.getLogin(),
                userBody.getName(),
                userBody.getBirthday()
        );
        users.add(newUser);

        log.info("Film created: {}", newUser);

        return ResponseEntity
                .created(URI.create("/api/users/" + newUser.getId()))
                .body(newUser);
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User userToUpdate = users.stream()
                .filter(f -> Objects.equals(f.getId(), user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));


        userToUpdate.setName(user.getName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setBirthday(user.getBirthday());
        userToUpdate.setLogin(user.getLogin());

        log.info("User updated: {}", userToUpdate);

        return ResponseEntity.ok(userToUpdate);
    }

    @DeleteMapping()
    public ResponseEntity<User> deleteFilm(@RequestBody User user) {
        users.remove(user);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public  ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(users);
    }

}
