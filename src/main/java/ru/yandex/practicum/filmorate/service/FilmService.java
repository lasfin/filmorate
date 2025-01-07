package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film filmBody) {
        return filmStorage.createFilm(filmBody);
    }

    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    public ResponseEntity<Film> deleteFilm(@RequestBody Film film) {
        return filmStorage.deleteFilm(film);
    }

    public ResponseEntity<List<Film>> getFilms() {
        return filmStorage.getFilms();
    }
}
