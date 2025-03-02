package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepo {
    List<Genre> getAllGenres();
    Optional<Genre> getGenreById(Integer id);
}
