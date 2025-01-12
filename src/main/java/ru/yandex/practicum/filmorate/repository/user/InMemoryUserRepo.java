package ru.yandex.practicum.filmorate.repository.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.util.*;

@Component
public class InMemoryUserRepo implements UserRepo {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepo.class);
    private Long nextId = 0L;
    private HashMap<Long, User> users = new HashMap<>();
    private HashMap<Long, Set<Long>> friendsIds = new HashMap<>();

    @Override
    public ResponseEntity<User> createUser(User user) {
        long nextIdUpdated = ++nextId;
        // Add initialization of friendsIds
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

        return ResponseEntity
                .created(URI.create("/users/" + newUser.getId()))
                .body(newUser);
    }

    @Override
    public ResponseEntity<User> updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("UserNotFoundException user {}", user.getId());
            
            throw new UserNotFoundException("User not found");
        }

        users.put(user.getId(), user);

        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<User> deleteUser(User user) throws UserNotFoundException {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("User not found");
        }
        users.remove(user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }

    @Override
    public ResponseEntity<User> getUser(Long userId) throws UserNotFoundException {
        return ResponseEntity.ok(Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @Override
    public ResponseEntity<User> addFriend(Long userId, Long friendId) throws UserNotFoundException {
        log.info("in memory Adding friend {} to user {}", friendId, userId);

        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            log.warn("UserNotFoundException use {} friend {}", friendId, userId);
            throw new UserNotFoundException("User or friend not found");
        } else {
            log.info("User and friend found");
        }

        friendsIds.get(userId).add(friendId);
        friendsIds.get(friendId).add(userId);

        return ResponseEntity.ok(users.get(userId));
    }

    @Override
    public ResponseEntity<User> removeFriend(Long userId, Long friendId) throws UserNotFoundException {
        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            throw new UserNotFoundException("User or friend not found");
        }

        friendsIds.get(userId).remove(friendId);
        friendsIds.get(friendId).remove(userId);

        return ResponseEntity.ok(users.get(userId));
    }

    @Override
    public ResponseEntity<List<User>> getFriends(Long userId) throws UserNotFoundException {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("User or friend not found");
        }

        Set<Long> friendIds = friendsIds.get(userId);
        List<User> friends = new ArrayList<>();
        for (Long friendId : friendIds) {
            friends.add(users.get(friendId));
        }

        return ResponseEntity.ok(friends);
    }

    @Override
    public ResponseEntity<List<User>> getCommonFriends(Long userId, Long friendId) throws UserNotFoundException {
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
        for (Long commonFriendId : userFriends) {
            commonFriends.add(users.get(commonFriendId));
        }

        return ResponseEntity.ok(commonFriends);
    }
}