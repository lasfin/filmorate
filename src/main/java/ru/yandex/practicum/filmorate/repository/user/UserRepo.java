package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserRepo {

    User createUser(User user);

    User updateUser(User user);

    User deleteUser(User user);

    User getUser(Long userId);

    List<User> getUsers();

    User addFriend(Long userId, Long friendId);

    User removeFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long friendId);
}
