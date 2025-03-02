package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User createUser(@Valid @RequestBody User userBody) {
        return userRepo.createUser(userBody);
    }

    public User updateUser(@RequestBody User user) {
        if (userRepo.getUser(user.getId()) == null) {
            throw new UserNotFoundException("User not found" + user.getId());
        }

        return userRepo.updateUser(user);
    }

    public User deleteUser(@RequestBody User user) {
        if (userRepo.getUser(user.getId()) == null) {
            throw new UserNotFoundException("User not found" + user.getId());
        }

        return userRepo.deleteUser(user);
    }

    public User getUser(Long userId) {
        if (userRepo.getUser(userId) == null) {
            throw new UserNotFoundException("User not found" + userId);
        }
        return userRepo.getUser(userId);
    }

    public List<User> getUsers() {
        return userRepo.getUsers();
    }

    public User addFriend(Long userId, Long friendId) {
        log.info("user service. Adding friend {} to user {}", friendId, userId);

        if (userId.equals(friendId)) {
            log.warn("User and friend are the same user {} friend {}", friendId, userId);
            throw new UserNotFoundException("User and friend are the same");
        }

        if (userRepo.getUser(userId) == null || userRepo.getUser(friendId) == null) {
            log.warn("UserNotFoundException use {} friend {}", friendId, userId);
            throw new UserNotFoundException("User or friend not found");
        }

        return userRepo.addFriend(userId, friendId);
    }

    public User removeFriend(Long userId, Long friendId) {
        final User user = userRepo.getUser(userId);
        final User friend = userRepo.getUser(friendId);

        if (user == null || friend == null) {
            log.warn("UserNotFoundException use {} friend {}", friendId, userId);
            throw new UserNotFoundException("User or friend not found");
        }

        return userRepo.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        final User user = userRepo.getUser(userId);

        if (user == null) {
            throw new UserNotFoundException("User not found " + userId);
        }

        return userRepo.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        final User user = userRepo.getUser(userId);
        final User friend = userRepo.getUser(friendId);

        if (user == null || friend == null) {
            throw new UserNotFoundException("Friend or user not found");
        }

        return userRepo.getCommonFriends(userId, friendId);
    }
}
