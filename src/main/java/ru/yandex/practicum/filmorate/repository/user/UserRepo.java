package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserRepo {

    ResponseEntity<User> createUser(User user);

    ResponseEntity<User> updateUser(User user);

    ResponseEntity<User> deleteUser(User user);

    ResponseEntity<User> getUser(Long userId);

    ResponseEntity<List<User>> getUsers();

    ResponseEntity<User> addFriend(Long userId, Long friendId);

    ResponseEntity<User> removeFriend(Long userId, Long friendId);

    ResponseEntity<List<User>> getFriends(Long userId);

    ResponseEntity<List<User>> getCommonFriends(Long userId, Long friendId);
}
