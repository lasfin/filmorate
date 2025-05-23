package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import ru.yandex.practicum.filmorate.model.validations.AfterFirstFilm;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedHashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Long id;
    @NotNull(message = "Name is required")
    @Size(min = 1, message = "Name should be at least 1 character long")
    private String name;
    @NotNull(message = "Description is required")
    @Size(min = 1, max = 200, message = "Description should be between 1 and 200 character long")
    private String description;
    @NotNull(message = "Release date is required")
    @AfterFirstFilm
    private LocalDate releaseDate;
    @Min(value = 1, message = "Duration should be greater than 0")
    private int duration;

    private MpaRating mpa;
    private Set<Genre> genres = new LinkedHashSet<>();
    private Set<Long> likes = new HashSet<>();
}
