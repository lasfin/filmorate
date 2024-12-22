package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Must be a valid email address")
    private String email;
    @NotEmpty(message = "Login cannot be empty")
    @Pattern(regexp = "^\\S+$",
            message = "Login cannot contain whitespace characters")
    private String login;
    private String name;
    @PastOrPresent(message = "Birthday should be in the past or present")
    private LocalDate birthday;
}
