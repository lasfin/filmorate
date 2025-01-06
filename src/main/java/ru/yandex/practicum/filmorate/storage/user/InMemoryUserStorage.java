package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private Long nextId = 1L;
    private ArrayList<User> users = new ArrayList<>();

    @Override
    public ResponseEntity<User> createUser(User user) {
        User newUser = new User(
                nextId++,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                Set.of()
        );
        users.add(newUser);

        log.info("Film created: {}", newUser);

        return ResponseEntity
                .created(URI.create("/users/" + newUser.getId()))
                .body(newUser);
    }

    @Override
    public ResponseEntity<User> updateUser(User user) {
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

    @Override
    public ResponseEntity<User> deleteUser(User user) {
        users.remove(user);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(users);
    }
}
