package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Primary
@Repository
public class H2UserRepo implements UserRepo {

    private final JdbcTemplate jdbcTemplate;

    public H2UserRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        // Get the next ID from a sequence or max + 1
        Long nextId = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(user_id), 0) + 1 FROM users", Long.class);
        user.setId(nextId);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                    new String[]{"user_id"}
            );
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(userId);

        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );

        return getUser(user.getId());
    }

    @Override
    public User deleteUser(User user) {
        // Delete references in friendships table
        jdbcTemplate.update("DELETE FROM friendships WHERE user_id = ? OR friend_id = ?",
                user.getId(), user.getId());

        // Delete references in film_likes table
        jdbcTemplate.update("DELETE FROM film_likes WHERE user_id = ?", user.getId());

        // Delete the user
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", user.getId());

        return user;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query(
                "SELECT * FROM users",
                this::mapRowToUser
        );
    }

    @Override
    public User getUser(Long userId) {
        try {
            User user = jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE user_id = ?",
                    this::mapRowToUser,
                    userId
            );

            if (user != null) {
                loadUserFriends(user);
            }

            return user;
        } catch (EmptyResultDataAccessException e) {
            // No user found with the given ID
            return null;
        }
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        // Check if the friendship already exists
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?",
                Integer.class,
                userId, friendId
        );

        // Only insert if it doesn't exist
        if (count == 0) {
            jdbcTemplate.update(
                    "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, ?)",
                    userId, friendId, false
            );
        }

        return getUser(userId);
    }

    @Override
    public User removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update(
                "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?",
                userId, friendId
        );

        return getUser(userId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        return jdbcTemplate.query(
                "SELECT u.* FROM users u " +
                        "JOIN friendships f ON u.user_id = f.friend_id " +
                        "WHERE f.user_id = ?",
                this::mapRowToUser,
                userId
        );
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return jdbcTemplate.query(
                "SELECT u.* FROM users u " +
                        "WHERE u.user_id IN (" +
                        "  SELECT f1.friend_id FROM friendships f1 " +
                        "  JOIN friendships f2 ON f1.friend_id = f2.friend_id " +
                        "  WHERE f1.user_id = ? AND f2.user_id = ?" +
                        ")",
                this::mapRowToUser,
                userId, otherUserId
        );
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        // Friends will be loaded separately
        user.setFriends(new HashSet<>());

        return user;
    }

    private void loadUserFriends(User user) {
        List<Long> friends = jdbcTemplate.query(
                "SELECT friend_id FROM friendships WHERE user_id = ?",
                (rs, rowNum) -> rs.getLong("friend_id"),
                user.getId()
        );

        user.setFriends(new HashSet<>(friends));
    }
}