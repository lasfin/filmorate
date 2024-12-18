package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/films")
@RequiredArgsConstructor
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private Long nextId = 1L;
    private ArrayList<Film> films = new ArrayList<>();

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film filmBody) {
        Film newFilm = new Film(
                nextId++,
                filmBody.getName(),
                filmBody.getDescription(),
                filmBody.getReleaseDate(),
                filmBody.getDuration()
        );
        films.add(newFilm);

        log.info("Film created: {}", newFilm);

        return ResponseEntity
                .created(URI.create("/api/films/" + newFilm.getId()))
                .body(newFilm);
    }

    @PutMapping()
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        Film filmToUpdate = films.stream()
                .filter(f -> Objects.equals(f.getId(), film.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Film not found"));


        filmToUpdate.setName(film.getName());
        filmToUpdate.setDescription(film.getDescription());
        filmToUpdate.setReleaseDate(film.getReleaseDate());
        filmToUpdate.setDuration(film.getDuration());

        log.info("Film updated: {}", filmToUpdate);

        return ResponseEntity.ok(filmToUpdate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Film> deleteFilm(@RequestBody Film film) {
        films.remove(film);

        return ResponseEntity.noContent().build();
    }

    public  ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.ok(films);
    }

}
