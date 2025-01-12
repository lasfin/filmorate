package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.user.InMemoryUserRepo;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.repository.film.InMemoryFilmRepo;

import java.time.LocalDate;
import java.util.List;

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
        // Create a test film with valid data
        testFilm = new Film(
                null,
                "Test Film",
                "Test Description",
                LocalDate.of(2023, 1, 1),
                120
        );
    }

    @Test
    void createFilm_ShouldCreateNewFilm() {
        // When
        ResponseEntity<Film> response = filmController.createFilm(testFilm);
        Film createdFilm = response.getBody();

        // Then
        assertNotNull(createdFilm);
        assertEquals(1L, createdFilm.getId());
        assertEquals(testFilm.getName(), createdFilm.getName());
        assertEquals(testFilm.getDescription(), createdFilm.getDescription());
        assertEquals(testFilm.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(testFilm.getDuration(), createdFilm.getDuration());
        assertEquals(201, response.getStatusCodeValue());
    }

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

        // When
        ResponseEntity<Film> updateResponse = filmController.updateFilm(filmToUpdate);
        Film updatedFilm = updateResponse.getBody();

        // Then
        assertNotNull(updatedFilm);
        assertEquals("Updated Name", updatedFilm.getName());
        assertEquals("Updated Description", updatedFilm.getDescription());
        assertEquals(200, updateResponse.getStatusCodeValue());
    }

    @Test
    void updateFilm_ShouldThrowException_WhenFilmNotFound() {
        // Given
        Film nonExistentFilm = new Film(999L, "Non-existent", "Description",
                LocalDate.now(), 120);

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
        assertEquals(204, deleteResponse.getStatusCodeValue());
        assertEquals(0, getResponse.getBody().size());
    }

    @Test
    void getFilms_ShouldReturnAllFilms() {
        // Given
        filmController.createFilm(testFilm);
        filmController.createFilm(new Film(null, "Second Film", "Description",
                LocalDate.now(), 90));

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