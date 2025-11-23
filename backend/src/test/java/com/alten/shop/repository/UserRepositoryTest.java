package com.alten.shop.repository;

import com.alten.shop.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenSaveUser_thenUserIsPersisted() {
        // Given
        User user = User.builder()
                .username("johndoe")
                .firstname("John")
                .email("john@example.com")
                .password("encodedPassword")
                .build();

        // When
        User saved = userRepository.save(user);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        // Given
        User user = User.builder()
                .username("johndoe")
                .firstname("John")
                .email("john@example.com")
                .password("encodedPassword")
                .build();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmail("john@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("johndoe");
    }

    @Test
    void whenFindByNonExistentEmail_thenReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void whenExistsByEmail_thenReturnTrue() {
        // Given
        User user = User.builder()
                .username("johndoe")
                .firstname("John")
                .email("john@example.com")
                .password("encodedPassword")
                .build();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmail("john@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByNonExistentEmail_thenReturnFalse() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void whenFindAll_thenReturnAllUsers() {
        // Given
        User user1 = User.builder()
                .username("user1")
                .firstname("User")
                .email("user1@example.com")
                .password("password1")
                .build();
        User user2 = User.builder()
                .username("user2")
                .firstname("User")
                .email("user2@example.com")
                .password("password2")
                .build();
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // When
        var users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(2);
    }
}
