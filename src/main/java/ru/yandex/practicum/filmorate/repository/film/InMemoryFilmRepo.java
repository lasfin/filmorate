package ru.yandex.practicum.filmorate.repository.film;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
public class InMemoryFilmRepo implements FilmRepo {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private Long nextId = 1L;
    private ArrayList<Film> films = new ArrayList<>();
    private HashMap<Long, Set<Long>> filmLikes = new HashMap<>();

    public Film createFilm(@Valid @RequestBody Film filmBody) {
        Film newFilm = new Film(
                nextId++,
                filmBody.getName(),
                filmBody.getDescription(),
                filmBody.getReleaseDate(),
                filmBody.getDuration(),
                filmBody.getMpaRating(),
                filmBody.getGenres(),
                filmBody.getLikes()
        );
        films.add(newFilm);

        log.info("Film created: {}", newFilm);
        filmLikes.put(newFilm.getId(), new HashSet<>());

        return newFilm;
    }

    public Film getFilm(Long filmId) {
        Optional<Film> film = films.stream()
                .filter(f -> Objects.equals(f.getId(), filmId))
                .findFirst();

        return film.orElse(null);
    }

    public Film updateFilm(@RequestBody Film film) {
        Optional<Film> optionalFilm = films.stream()
                .filter(f -> Objects.equals(f.getId(), film.getId()))
                .findFirst();

        if (optionalFilm.isEmpty()) {
            return null;
        }

        Film filmToUpdate = optionalFilm.get();
        filmToUpdate.setName(film.getName());
        filmToUpdate.setDescription(film.getDescription());
        filmToUpdate.setReleaseDate(film.getReleaseDate());
        filmToUpdate.setDuration(film.getDuration());

        log.info("Film updated: {}", filmToUpdate);

        return filmToUpdate;
    }

    public Film deleteFilm(@RequestBody Film film) {
        Optional<Film> filmToDelete = films.stream()
                .filter(f -> Objects.equals(f.getId(), film.getId()))
                .findFirst();

        if (filmToDelete.isEmpty()) {
            return null;
        }

        films.remove(film);

        return filmToDelete.get();
    }

    public List<Film> getFilms() {
        return films;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Optional<Film> film = films.stream()
                .filter(f -> Objects.equals(f.getId(), filmId))
                .findFirst();

        if (film.isEmpty()) {
            return null;
        }

        filmLikes.get(filmId).add(userId);
        log.info("User {} liked film {}", userId, filmId);

        return film.get();
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        Optional<Film> optionalFilm = films.stream()
                .filter(f -> Objects.equals(f.getId(), filmId))
                .findFirst();

        if (!filmLikes.get(filmId).remove(userId)) {
            return null;
        }
        if (optionalFilm.isEmpty()) {
            return null;
        }

        log.info("User {} removed like from film {}", userId, filmId);

        return optionalFilm.get();
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        List<Film> popularFilms = films.stream()
                .sorted((f1, f2) -> Integer.compare(
                        filmLikes.get(f2.getId()).size(),
                        filmLikes.get(f1.getId()).size()))
                .limit(count)
                .collect(Collectors.toList());

        return popularFilms;
    }

}
