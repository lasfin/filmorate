package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepo;
import ru.yandex.practicum.filmorate.repository.user.UserRepo;

import java.util.List;

@Service
public class FilmService {
    private final FilmRepo filmRepo;
    private final UserRepo userRepo;

    @Autowired
    public FilmService(FilmRepo filmRepo, UserRepo userRepo) {
        this.filmRepo = filmRepo;
        this.userRepo = userRepo;
    }

    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film filmBody) {
        return filmRepo.createFilm(filmBody);
    }

    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        return filmRepo.updateFilm(film);
    }

    public ResponseEntity<Film> deleteFilm(@RequestBody Film film) {
        return filmRepo.deleteFilm(film);
    }

    public ResponseEntity<List<Film>> getFilms() {
        return filmRepo.getFilms();
    }

    public ResponseEntity<Film> addLike(Long filmId, Long userId) {
        ResponseEntity<User> userResponse = userRepo.getUser(userId);
        if (userResponse.getStatusCode().is4xxClientError()) {
            throw new UserNotFoundException("User not found");
        }
        return filmRepo.addLike(filmId, userId);
    }

    public ResponseEntity<Film> removeLike(Long filmId, Long userId) {
        return filmRepo.removeLike(filmId, userId);
    }

    public ResponseEntity<List<Film>> getPopularFilms(Integer count) {
        return filmRepo.getPopularFilms(count);
    }
}
