package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BasicErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film filmBody) {
        return filmStorage.createFilm(filmBody);
    }

    @PutMapping()
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @DeleteMapping()
    public ResponseEntity<Film> deleteFilm(@RequestBody Film film) {
        return filmStorage.deleteFilm(film);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        return filmStorage.getFilms();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(Exception e) {
        return new BasicErrorResponse("Bad request", e.getMessage());
    }

}
