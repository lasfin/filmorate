package ru.yandex.practicum.filmorate.repository.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserRepo implements UserRepo {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepo.class);
    private Long nextId = 0L;
    private HashMap<Long, User> users = new HashMap<>();
    private HashMap<Long, Set<Long>> friendsIds = new HashMap<>();

    @Override
    public User createUser(User user) {
        long nextIdUpdated = ++nextId;
        if (!friendsIds.containsKey(nextIdUpdated)) {
            friendsIds.put(nextIdUpdated, new HashSet<>());
        }

        User newUser = new User(
                nextIdUpdated,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        users.put(nextIdUpdated, newUser);

        return newUser;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("UserNotFoundException user {}", user.getId());

            throw new UserNotFoundException("User not found");
        }

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User deleteUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("User not found");
        }
        User userToReturn = users.get(user.getId());
        users.remove(user.getId());

        return userToReturn;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long userId) {
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    // todo:
    // Отсутствие данных при выборке - это ошибка в логике - с точки зрения хранилища ничего страшного нет,
    // просто нет данных. Но именно сервис должен воспринять это как ошибку и кинуть эксепшен. Как-то так )
    @Override
    public User addFriend(Long userId, Long friendId) throws UserNotFoundException {
        log.info("in memory Adding friend {} to user {}", friendId, userId);

        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            log.warn("UserNotFoundException use {} friend {}", friendId, userId);
            throw new UserNotFoundException("User or friend not found");
        } else {
            log.info("User and friend found");
        }

        friendsIds.get(userId).add(friendId);
        friendsIds.get(friendId).add(userId);

        return users.get(userId);
    }

    @Override
    public User removeFriend(Long userId, Long friendId) throws UserNotFoundException {
        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            throw new UserNotFoundException("User or friend not found");
        }

        friendsIds.get(userId).remove(friendId);
        friendsIds.get(friendId).remove(userId);

        return users.get(userId);
    }

    @Override
    public List<User> getFriends(Long userId) throws UserNotFoundException {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("User or friend not found");
        }

        Set<Long> friendIds = friendsIds.get(userId);
        List<User> friends = new ArrayList<>();
        for (Long friendId : friendIds) {
            friends.add(users.get(friendId));
        }

        return friends;
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) throws UserNotFoundException {
        final User user = users.get(userId);
        final User friend = users.get(friendId);

        if (user == null || friend == null) {
            throw new UserNotFoundException("User not found");
        }

        Set<Long> userFriends = friendsIds.get(userId);
        Set<Long> friendFriends = friendsIds.get(friendId);

        Set<Long> commonIds = new HashSet<>(userFriends);
        commonIds.retainAll(friendFriends);

        List<User> commonFriends = new ArrayList<>();
        for (Long commonFriendId : commonIds) {
            commonFriends.add(users.get(commonFriendId));
        }

        return commonFriends;
    }
}