package ru.yandex.practicum.filmorate.model.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AfterFirstFilmValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterFirstFilm {
    String message() default "Release date must be after December 28, 1895";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

