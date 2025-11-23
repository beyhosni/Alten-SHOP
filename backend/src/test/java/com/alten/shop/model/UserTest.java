package com.alten.shop.model;

import jakarta.persistence.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidUser_thenNoConstraintViolations() {
        // Given
        User user = User.builder()
                .username("johndoe")
                .firstname("John")
                .email("john@example.com")
                .password("encodedPassword123")
                .build();

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void whenUsernameIsBlank_thenConstraintViolation() {
        // Given
        User user = User.builder()
                .username("")
                .firstname("John")
                .email("john@example.com")
                .password("password123")
                .build();

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("blank");
    }

    @Test
    void whenEmailIsInvalid_thenConstraintViolation() {
        // Given
        User user = User.builder()
                .username("johndoe")
                .firstname("John")
                .email("invalid-email")
                .password("password123")
                .build();

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    void whenEmailIsBlank_thenConstraintViolation() {
        // Given
        User user = User.builder()
                .username("johndoe")
                .firstname("John")
                .email("")
                .password("password123")
                .build();

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenPasswordIsBlank_thenConstraintViolation() {
        // Given
        User user = User.builder()
                .username("johndoe")
                .firstname("John")
                .email("john@example.com")
                .password("")
                .build();

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    void whenCreatingUser_thenTimestampsAreSet() {
        // Given
        User user = new User();
        user.setUsername("johndoe");
        user.setFirstname("John");
        user.setEmail("john@example.com");
        user.setPassword("password123");

        // When
        user.onCreate();

        // Then
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    void whenUpdatingUser_thenUpdatedAtIsSet() throws InterruptedException {
        // Given
        User user = new User();
        user.setUsername("johndoe");
        user.setFirstname("John");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.onCreate();

        Thread.sleep(10); // Small delay to ensure different timestamps

        // When
        user.onUpdate();

        // Then
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isAfter(user.getCreatedAt());
    }
}
