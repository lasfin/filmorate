package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private Long nextId = 0L;
    private HashMap<Long, User> users = new HashMap<>();

    @Override
    public ResponseEntity<User> createUser(User user) {
        long nextIdUpdated = nextId + 1;
        nextId++;

        User newUser = new User(
                nextIdUpdated,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        users.put(nextIdUpdated, newUser);

        return ResponseEntity
                .created(URI.create("/users/" + newUser.getId()))
                .body(newUser);
    }

    @Override
    public ResponseEntity<User> updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new RuntimeException("User not found");
        }

        users.put(user.getId(), user);

        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<User> deleteUser(User user) {
        users.remove(user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }
}