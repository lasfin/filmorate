package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

@Service
public class UserService {
    public User addToFriends(User user, User friend) {
        return user.addFriend(friend);
    }

    public User removeFromFriends(User user, User friend) {
        return user.removeFriend(friend);
    }
}
