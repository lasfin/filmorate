package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadRequestResponse;
import ru.yandex.practicum.filmorate.exceptions.NotFoundResponse;
import ru.yandex.practicum.filmorate.exceptions.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User userBody) {
        return ResponseEntity.ok(userService.createUser(userBody));
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @DeleteMapping()
    public ResponseEntity<User> deleteUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.deleteUser(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        return ResponseEntity.ok(userService.addFriend(userId, friendId));
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        return ResponseEntity.ok(userService.removeFriend(userId, friendId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFriends(userId));
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable Long userId, @PathVariable Long friendId) {
        return ResponseEntity.ok(userService.getCommonFriends(userId, friendId));
    }

    // todo:
    // Кстати, лучше создать один класс исключение NotFoundException и в описании ошибки уже писать с
    // чем связано исключение с user, film и тд в больших проектах может быть сотни сущностей,
    // зачем на каждую из них создавать отдельное исключение.
    // Лучше сделать по одному исключению на каждый тип ошибки, а не сущности.
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        log.warn("User not found");
        log.warn(e.getMessage());

        return new NotFoundResponse(
                "Error",
                e.getMessage()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final Exception e) {
        return new BadRequestResponse("Bad request", e.getMessage());
    }
}
