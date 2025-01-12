package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmRepo {

    ResponseEntity<Film> createFilm(Film film);

    ResponseEntity<Film> updateFilm(Film film);

    ResponseEntity<Film> deleteFilm(Film film);

    ResponseEntity<List<Film>> getFilms();

    ResponseEntity<Film> addLike(Long filmId, Long userId);

    ResponseEntity<Film> removeLike(Long filmId, Long userId);

    ResponseEntity<List<Film>> getPopularFilms(Integer count);
}
