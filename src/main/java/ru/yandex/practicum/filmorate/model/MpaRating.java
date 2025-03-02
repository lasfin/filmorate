package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpaRating {
    private Long id;
    @NotNull(message = "Name is required")
    @Size(min = 1, message = "Name should be at least 1 character long")
    private String name;
}
