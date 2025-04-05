package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadRequestResponse;
import ru.yandex.practicum.filmorate.exceptions.NotFoundResponse;
import ru.yandex.practicum.filmorate.exceptions.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.genre.InvalidGenreException;
import ru.yandex.practicum.filmorate.exceptions.mpa.InvalidMpaException;
import ru.yandex.practicum.filmorate.exceptions.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable Long id) {
        Film film = filmService.getFilmById(id);
        return ResponseEntity.ok(film);
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film filmBody) {
        Film film = filmService.createFilm(filmBody);
        URI location = URI.create("/films/" + film.getId());

        return ResponseEntity.created(location).body(film);
    }

    @PutMapping()
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        Film updatedFilm = filmService.updateFilm(film);
        if (updatedFilm == null) {
            throw new FilmNotFoundException("Film not found: " + film.getId());
        }
        return ResponseEntity.ok(updatedFilm);
    }

    @DeleteMapping()
    public ResponseEntity<Film> deleteFilm(@RequestBody Film film) {
        return ResponseEntity.ok(filmService.deleteFilm(film));
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.ok(filmService.getFilms());
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return ResponseEntity.ok(filmService.addLike(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> removeLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return ResponseEntity.ok(filmService.removeLike(id, userId));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(
            @RequestParam(defaultValue = "10") Integer count) {
        return ResponseEntity.ok(filmService.getPopularFilms(count));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(final UserNotFoundException e) {
        return new NotFoundResponse("Not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidMpaException(final InvalidMpaException e) {
        return new NotFoundResponse("Not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidGenreException(final InvalidGenreException e) {
        return new NotFoundResponse("Not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final Exception e) {
        return new BadRequestResponse("Bad request", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFilmNotFoundException(final FilmNotFoundException e) {
        return new BadRequestResponse("Internal server error", e.getMessage());
    }
}
