package ru.yandex.practicum.filmorate.repository.film;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmRepo implements FilmRepo {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private Long nextId = 1L;
    private ArrayList<Film> films = new ArrayList<>();
    private HashMap<Long, Set<Long>> filmLikes = new HashMap<>();

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
        filmLikes.put(newFilm.getId(), new HashSet<>());

        return ResponseEntity
                .created(URI.create("/films/" + newFilm.getId()))
                .body(newFilm);
    }

    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        Film filmToUpdate = films.stream()
                .filter(f -> Objects.equals(f.getId(), film.getId()))
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException("Film not found"));

        filmToUpdate.setName(film.getName());
        filmToUpdate.setDescription(film.getDescription());
        filmToUpdate.setReleaseDate(film.getReleaseDate());
        filmToUpdate.setDuration(film.getDuration());

        log.info("Film updated: {}", filmToUpdate);

        return ResponseEntity.ok(filmToUpdate);
    }

    public ResponseEntity<Film> deleteFilm(@RequestBody Film film) {
        films.remove(film);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.ok(films);
    }

    @Override
    public ResponseEntity<Film> addLike(Long filmId, Long userId) {
        Film film = films.stream()
                .filter(f -> Objects.equals(f.getId(), filmId))
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException("Film not found"));

        // check if user exists


        filmLikes.get(filmId).add(userId);
        log.info("User {} liked film {}", userId, filmId);

        return ResponseEntity.ok(film);
    }

    @Override
    public ResponseEntity<Film> removeLike(Long filmId, Long userId) {
        Film film = films.stream()
                .filter(f -> Objects.equals(f.getId(), filmId))
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException("Film not found"));

        if (!filmLikes.get(filmId).remove(userId)) {
            throw new FilmNotFoundException("Like not found");
        }

        log.info("User {} removed like from film {}", userId, filmId);

        return ResponseEntity.ok(film);
    }

    @Override
    public ResponseEntity<List<Film>> getPopularFilms(Integer count) {
        List<Film> popularFilms = films.stream()
                .sorted((f1, f2) -> Integer.compare(
                        filmLikes.get(f2.getId()).size(),
                        filmLikes.get(f1.getId()).size()))
                .limit(count)
                .collect(Collectors.toList());

        return ResponseEntity.ok(popularFilms);
    }

}
