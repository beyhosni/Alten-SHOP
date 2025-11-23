package com.alten.shop.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ContactTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoConstraintViolations() {
        // Given
        Contact contact = Contact.builder()
                .email("test@example.com")
                .message("This is a test message")
                .build();

        // When
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void whenEmailIsNull_thenConstraintViolation() {
        // Given
        Contact contact = Contact.builder()
                .message("This is a test message")
                .build();

        // When
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void whenEmailIsInvalid_thenConstraintViolation() {
        // Given
        Contact contact = Contact.builder()
                .email("invalid-email")
                .message("This is a test message")
                .build();

        // When
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void whenMessageIsBlank_thenConstraintViolation() {
        // Given
        Contact contact = Contact.builder()
                .email("test@example.com")
                .message("")
                .build();

        // When
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("message"));
    }

    @Test
    void whenMessageExceeds300Characters_thenConstraintViolation() {
        // Given
        String longMessage = "a".repeat(301);
        Contact contact = Contact.builder()
                .email("test@example.com")
                .message(longMessage)
                .build();

        // When
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("message"));
    }

    @Test
    void whenMessageIs300Characters_thenNoConstraintViolation() {
        // Given
        String maxMessage = "a".repeat(300);
        Contact contact = Contact.builder()
                .email("test@example.com")
                .message(maxMessage)
                .build();

        // When
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void whenCreatingContact_thenTimestampIsSet() {
        // Given & When
        Contact contact = new Contact();
        contact.setEmail("test@example.com");
        contact.setMessage("Test message");

        // Then
        assertThat(contact.getCreatedAt()).isNotNull();
    }
}
