package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Long id;
    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "Description is required")
    private String description;
    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;
    @Min(value = 1, message = "Duration should be greater than 0")
    private int duration;
}
