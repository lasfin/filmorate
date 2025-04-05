package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.user.InMemoryUserRepo;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.repository.film.InMemoryFilmRepo;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Film testFilm;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmService = new FilmService(
                new InMemoryFilmRepo(), new InMemoryUserRepo()
        );
        filmController = new FilmController(filmService);

        // Initialize MPA rating and genres
        MpaRating mpaRating = new MpaRating(1L, "G");
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1L, "Comedy"));
        Set<Long> likes = new HashSet<>();

        // Create a test film with valid data using the full constructor
        testFilm = new Film(
                null,
                "Test Film",
                "Test Description",
                LocalDate.of(2023, 1, 1),
                120,
                mpaRating,
                genres,
                likes
        );
    }

//    @Test
//    void createFilm_ShouldCreateNewFilm() {
//        // When
//        ResponseEntity<Film> response = filmController.createFilm(testFilm);
//        Film createdFilm = response.getBody();
//
//        // Then
//        assertNotNull(createdFilm);
//        assertEquals(1L, createdFilm.getId());
//        assertEquals(testFilm.getName(), createdFilm.getName());
//        assertEquals(testFilm.getDescription(), createdFilm.getDescription());
//        assertEquals(testFilm.getReleaseDate(), createdFilm.getReleaseDate());
//        assertEquals(testFilm.getDuration(), createdFilm.getDuration());
//        assertEquals(testFilm.getMpa(), createdFilm.getMpa());
//        assertEquals(testFilm.getGenres(), createdFilm.getGenres());
//        assertTrue(createdFilm.getLikes().isEmpty());
//        assertEquals(201, response.getStatusCodeValue());
//    }

    @Test
    void createFilm_ShouldIncrementIds() {
        // When
        ResponseEntity<Film> response1 = filmController.createFilm(testFilm);
        ResponseEntity<Film> response2 = filmController.createFilm(testFilm);

        // Then
        assertEquals(1L, response1.getBody().getId());
        assertEquals(2L, response2.getBody().getId());
    }

    @Test
    void updateFilm_ShouldUpdateExistingFilm() {
        // Given
        ResponseEntity<Film> createResponse = filmController.createFilm(testFilm);
        Film filmToUpdate = createResponse.getBody();
        assertNotNull(filmToUpdate);

        filmToUpdate.setName("Updated Name");
        filmToUpdate.setDescription("Updated Description");
        // Update new fields
        filmToUpdate.setMpa(new MpaRating(2L, "PG"));
        Set<Genre> updatedGenres = new HashSet<>();
        updatedGenres.add(new Genre(2L, "Drama"));
        filmToUpdate.setGenres(updatedGenres);
        // Add a like
        Set<Long> likes = new HashSet<>();
        likes.add(1L);
        filmToUpdate.setLikes(likes);

        // When
        ResponseEntity<Film> updateResponse = filmController.updateFilm(filmToUpdate);
        Film updatedFilm = updateResponse.getBody();

        // Then
        assertNotNull(updatedFilm);
        assertEquals("Updated Name", updatedFilm.getName());
        assertEquals("Updated Description", updatedFilm.getDescription());
        assertEquals(new MpaRating(2L, "PG"), updatedFilm.getMpa());
        assertEquals(updatedGenres, updatedFilm.getGenres());
        assertEquals(likes, updatedFilm.getLikes());
        assertEquals(200, updateResponse.getStatusCodeValue());
    }

    @Test
    void updateFilm_ShouldThrowException_WhenFilmNotFound() {
        // Given
        MpaRating mpaRating = new MpaRating(1L, "G");
        Set<Genre> genres = new HashSet<>();
        Set<Long> likes = new HashSet<>();

        Film nonExistentFilm = new Film(
                999L,
                "Non-existent",
                "Description",
                LocalDate.now(),
                120,
                mpaRating,
                genres,
                likes
        );

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            filmController.updateFilm(nonExistentFilm);
        });
    }

    @Test
    void deleteFilm_ShouldRemoveFilm() {
        // Given
        ResponseEntity<Film> createResponse = filmController.createFilm(testFilm);
        Film filmToDelete = createResponse.getBody();
        assertNotNull(filmToDelete);

        // When
        ResponseEntity<Film> deleteResponse = filmController.deleteFilm(filmToDelete);
        ResponseEntity<List<Film>> getResponse = filmController.getFilms();

        // Then
        assertEquals(200, deleteResponse.getStatusCodeValue());
        assertEquals(0, getResponse.getBody().size());
    }

    @Test
    void getFilms_ShouldReturnAllFilms() {
        // Given
        filmController.createFilm(testFilm);

        // Create second film with all required fields
        MpaRating secondMpaRating = new MpaRating(3L, "PG-13");
        Set<Genre> secondFilmGenres = new HashSet<>();
        secondFilmGenres.add(new Genre(3L, "Action"));
        Set<Long> secondFilmLikes = new HashSet<>();

        Film secondFilm = new Film(
                null,
                "Second Film",
                "Description",
                LocalDate.now(),
                90,
                secondMpaRating,
                secondFilmGenres,
                secondFilmLikes
        );

        filmController.createFilm(secondFilm);

        // When
        ResponseEntity<List<Film>> response = filmController.getFilms();
        List<Film> films = response.getBody();

        // Then
        assertNotNull(films);
        assertEquals(2, films.size());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getFilms_ShouldReturnEmptyList_WhenNoFilms() {
        // When
        ResponseEntity<List<Film>> response = filmController.getFilms();
        List<Film> films = response.getBody();

        // Then
        assertNotNull(films);
        assertTrue(films.isEmpty());
        assertEquals(200, response.getStatusCodeValue());
    }
}