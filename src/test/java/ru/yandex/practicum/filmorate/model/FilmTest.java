package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {
    private static Validator validator;
    private Film film;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1996, 1, 1));
        film.setDuration(120);
    }

    @Test
    @DisplayName("Should validate correct film data")
    void shouldValidateCorrectData() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Validation should pass for correct data");
    }

    @Test
    @DisplayName("Should fail on null name")
    void shouldFailOnNullName() {
        film.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Name is required",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should fail on empty name")
    void shouldFailOnEmptyName() {
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Name should be at least 1 character long",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should fail on null description")
    void shouldFailOnNullDescription() {
        film.setDescription(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Description is required",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should fail on empty description")
    void shouldFailOnEmptyDescription() {
        film.setDescription("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Description should be at least 1 character long",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should fail on null release date")
    void shouldFailOnNullReleaseDate() {
        film.setReleaseDate(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Release date is required",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should fail on release date before first film")
    void shouldFailOnReleaseDateBeforeFirstFilm() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Release date must be after December 28, 1895",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should fail on zero duration")
    void shouldFailOnZeroDuration() {
        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Duration should be greater than 0",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should fail on negative duration")
    void shouldFailOnNegativeDuration() {
        film.setDuration(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Duration should be greater than 0",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should handle multiple validation failures")
    void shouldHandleMultipleValidationFailures() {
        film.setName(null);
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        assertEquals(4, violations.size(),
                "Should have violations for name, description, release date, and duration");

        assertTrue(messages.contains("Name is required"));
        assertTrue(messages.contains("Description should be at least 1 character long"));
        assertTrue(messages.contains("Release date must be after December 28, 1895"));
        assertTrue(messages.contains("Duration should be greater than 0"));
    }
}