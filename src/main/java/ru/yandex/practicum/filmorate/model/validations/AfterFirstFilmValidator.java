package ru.yandex.practicum.filmorate.model.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AfterFirstFilmValidator implements ConstraintValidator<AfterFirstFilm, LocalDate> {
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true; // let @NotNull handle null validation if needed
        }
        return date.isAfter(FIRST_FILM_DATE);
    }
}