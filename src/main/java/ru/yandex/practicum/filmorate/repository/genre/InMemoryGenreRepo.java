package ru.yandex.practicum.filmorate.repository.genre;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Primary
@Repository
public class InMemoryGenreRepo implements GenreRepo {
    private final Map<Integer, Genre> genres = new HashMap<>();

    public InMemoryGenreRepo() {
        genres.put(1, new Genre(1L, "Комедия"));
        genres.put(2, new Genre(2L, "Драма"));
        genres.put(3, new Genre(3L, "Мультфильм"));
        genres.put(4, new Genre(4L, "Триллер"));
        genres.put(5, new Genre(5L, "Документальный"));
        genres.put(6, new Genre(6L, "Боевик"));
    }

    @Override
    public List<Genre> getAllGenres() {
        return new ArrayList<>(genres.values());
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        return Optional.ofNullable(genres.get(id));
    }
}