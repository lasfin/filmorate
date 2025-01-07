package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmRepo;

import java.util.List;

@Service
public class FilmService {
    private final FilmRepo filmRepo;

    @Autowired
    public FilmService(FilmRepo filmRepo) {
        this.filmRepo = filmRepo;
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
}
