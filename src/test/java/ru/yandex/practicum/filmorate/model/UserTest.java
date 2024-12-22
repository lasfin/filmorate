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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private static Validator validator;
    private User user;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@example.com");
        user.setLogin("validLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("Should validate correct user data")
    void shouldValidateCorrectData() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Validation should pass for correct data");
    }

    @Test
    @DisplayName("Should fail on empty email")
    void shouldFailOnEmptyEmail() {
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Email cannot be empty",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should fail on invalid email format")
    void shouldFailOnInvalidEmail() {
        user.setEmail("invalid-email");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Must be a valid email address",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should fail on empty login")
    void shouldFailOnEmptyLogin() {
        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());

        // Convert violations to a list of messages for easier assertion
        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertTrue(messages.contains("Login cannot be empty"));
        assertTrue(messages.contains("Login cannot contain whitespace characters"));
    }

    @Test
    @DisplayName("Should fail on login with spaces")
    void shouldFailOnLoginWithSpaces() {
        user.setLogin("invalid login");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Login cannot contain whitespace characters",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should fail on future birthday")
    void shouldFailOnFutureBirthday() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Birthday should be in the past or present",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should accept null name")
    void shouldAcceptNullName() {
        user.setName(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Name can be null");
    }

    @Test
    @DisplayName("Should handle multiple validation failures")
    void shouldHandleMultipleValidationFailures() {
        user.setEmail("");
        user.setLogin("invalid login");
        user.setBirthday(LocalDate.now().plusYears(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(3, violations.size(),
                "Should have violations for email, login, and birthday");
    }
}