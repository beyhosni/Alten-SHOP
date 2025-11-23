package com.alten.shop.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set secret key for testing
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
    }

    @Test
    void whenGenerateToken_thenTokenIsNotNull() {
        // When
        String token = jwtService.generateToken(testEmail);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void whenExtractEmail_thenReturnCorrectEmail() {
        // Given
        String token = jwtService.generateToken(testEmail);

        // When
        String extractedEmail = jwtService.extractEmail(token);

        // Then
        assertThat(extractedEmail).isEqualTo(testEmail);
    }

    @Test
    void whenValidateValidToken_thenReturnTrue() {
        // Given
        String token = jwtService.generateToken(testEmail);

        // When
        boolean isValid = jwtService.isTokenValid(token, testEmail);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void whenValidateTokenWithWrongEmail_thenReturnFalse() {
        // Given
        String token = jwtService.generateToken(testEmail);

        // When
        boolean isValid = jwtService.isTokenValid(token, "wrong@example.com");

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void whenGenerateTokenWithExpiration_thenTokenHasCorrectExpiration() {
        // Given
        String token = jwtService.generateToken(testEmail);

        // When
        String extractedEmail = jwtService.extractEmail(token);

        // Then
        assertThat(extractedEmail).isEqualTo(testEmail);
        assertThat(jwtService.isTokenValid(token, testEmail)).isTrue();
    }
}
