package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmRepo {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilm(Film film);

    List<Film> getFilms();

    Film getFilm(Long filmId);

    Film addLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

    List<Film> getPopularFilms(Integer count);
}
