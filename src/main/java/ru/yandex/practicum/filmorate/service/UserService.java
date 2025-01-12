package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepo;

import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public ResponseEntity<User> createUser(@Valid @RequestBody User userBody) {
        return userRepo.createUser(userBody);
    }

    public ResponseEntity<User> updateUser(@RequestBody User user) throws UserNotFoundException {
        return userRepo.updateUser(user);
    }

    public ResponseEntity<User> deleteUser(@RequestBody User user) {
        return userRepo.deleteUser(user);
    }

    public ResponseEntity<User> getUser(Long userId) {
        return userRepo.getUser(userId);
    }

    public ResponseEntity<List<User>> getUsers() {
        return userRepo.getUsers();
    }

    public ResponseEntity<User> addFriend(Long userId, Long friendId) throws UserNotFoundException {
        log.info("user service. Adding friend {} to user {}", friendId, userId);

        if (userId.equals(friendId)) {
            log.warn("User and friend are the same user {} friend {}", friendId, userId);

            throw new UserNotFoundException("User and friend are the same");
        }

        try {
            final User user = userRepo.getUser(userId).getBody();
            final User friend = userRepo.getUser(friendId).getBody();
        } catch (Exception e) {
            log.warn("UserNotFoundException use {} friend {}", friendId, userId);
            throw new UserNotFoundException("User or friend not found");
        } finally {
            userRepo.addFriend(userId, friendId);
        }

        return userRepo.getUser(userId);
    }

    public ResponseEntity<User> removeFriend(Long userId, Long friendId) throws UserNotFoundException {
        final User user = userRepo.getUser(userId).getBody();
        final User friend = userRepo.getUser(friendId).getBody();

        if (user == null || friend == null) {
            log.warn("UserNotFoundException use {} friend {}", friendId, userId);
            throw new UserNotFoundException("User or friend not found");
        }

        return userRepo.removeFriend(userId, friendId);
    }

    public ResponseEntity<List<User>> getFriends(Long userId) throws UserNotFoundException {
        final User user = userRepo.getUser(userId).getBody();

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return userRepo.getFriends(userId);
    }

    public ResponseEntity<List<User>> getCommonFriends(Long userId, Long friendId) throws UserNotFoundException {
        final User user = userRepo.getUser(userId).getBody();
        final User friend = userRepo.getUser(friendId).getBody();

        if (user == null || friend == null) {
            throw new UserNotFoundException("User or friend not found");
        }

        return userRepo.getCommonFriends(userId, friendId);
    }
}
