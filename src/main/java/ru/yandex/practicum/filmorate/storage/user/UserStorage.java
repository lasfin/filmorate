package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    ResponseEntity<User> createUser(User user);
    ResponseEntity<User> updateUser(User user);
    ResponseEntity<User> deleteUser(User user);
    ResponseEntity<List<User>> getUsers();
}
