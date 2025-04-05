package ru.yandex.practicum.filmorate.exceptions.genre;

public class InvalidGenreException extends RuntimeException {
    public InvalidGenreException(String message) {
        super(message);
    }
}