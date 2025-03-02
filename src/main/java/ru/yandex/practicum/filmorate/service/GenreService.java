package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.genre.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepo;

import java.util.List;

@Service
public class GenreService {
    private final GenreRepo genreStorage;

    public GenreService(GenreRepo genreRepo) {
        this.genreStorage = genreRepo;
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        return genreStorage.getGenreById(id)
                .orElseThrow(() -> new GenreNotFoundException(String.format("Genre with id %d not found", id)));
    }
}