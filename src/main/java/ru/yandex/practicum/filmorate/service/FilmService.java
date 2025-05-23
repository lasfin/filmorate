package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.film.FilmNotFoundException;
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

    public Film createFilm(@RequestBody Film filmBody) {
        return filmRepo.createFilm(filmBody);
    }

    public Film updateFilm(@RequestBody Film film) {
        if (filmRepo.getFilm(film.getId()) == null) {
            throw new FilmNotFoundException("Film not found: " + film.getId());
        }
        return filmRepo.updateFilm(film);
    }

    public Film deleteFilm(@RequestBody Film film) {
        if (filmRepo.getFilm(film.getId()) == null) {
            throw new FilmNotFoundException("Film not found: " + film.getId());
        }

        return filmRepo.deleteFilm(film);
    }

    public Film getFilmById(Long id) {
        Film film = filmRepo.getFilm(id);
        if (film == null) {
            throw new FilmNotFoundException("Film not found: " + id);
        }
        return film;
    }

    public List<Film> getFilms() {
        return filmRepo.getFilms();
    }

    public Film addLike(Long filmId, Long userId) {
        User user = userRepo.getUser(userId);
        Film film = filmRepo.getFilm(filmId);

        if (user == null) {
            throw new UserNotFoundException("User not found: " + userId);
        }
        if (film == null) {
            throw new FilmNotFoundException("Film not found: " + filmId);
        }

        return filmRepo.addLike(filmId, userId);
    }

    public Film removeLike(Long filmId, Long userId) {
        User user = userRepo.getUser(userId);
        Film film = filmRepo.getFilm(filmId);

        if (user == null) {
            throw new UserNotFoundException("User not found: " + userId);
        }
        if (film == null) {
            throw new FilmNotFoundException("Film not found: " + filmId);
        }

        return filmRepo.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmRepo.getPopularFilms(count);
    }
}
