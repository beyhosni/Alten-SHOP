package com.alten.shop.repository;

import com.alten.shop.model.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ContactRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void whenSaveContact_thenContactIsPersisted() {
        // Given
        Contact contact = Contact.builder()
                .email("test@example.com")
                .message("Test message")
                .build();

        // When
        Contact saved = contactRepository.save(contact);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getMessage()).isEqualTo("Test message");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void whenFindById_thenReturnContact() {
        // Given
        Contact contact = Contact.builder()
                .email("test@example.com")
                .message("Test message")
                .build();
        Contact saved = entityManager.persistAndFlush(contact);

        // When
        Optional<Contact> found = contactRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void whenFindAll_thenReturnAllContacts() {
        // Given
        Contact contact1 = Contact.builder()
                .email("test1@example.com")
                .message("Message 1")
                .build();
        Contact contact2 = Contact.builder()
                .email("test2@example.com")
                .message("Message 2")
                .build();
        entityManager.persist(contact1);
        entityManager.persist(contact2);
        entityManager.flush();

        // When
        List<Contact> contacts = contactRepository.findAll();

        // Then
        assertThat(contacts).hasSize(2);
    }

    @Test
    void whenFindByEmail_thenReturnContacts() {
        // Given
        Contact contact1 = Contact.builder()
                .email("same@example.com")
                .message("Message 1")
                .build();
        Contact contact2 = Contact.builder()
                .email("same@example.com")
                .message("Message 2")
                .build();
        Contact contact3 = Contact.builder()
                .email("different@example.com")
                .message("Message 3")
                .build();
        entityManager.persist(contact1);
        entityManager.persist(contact2);
        entityManager.persist(contact3);
        entityManager.flush();

        // When
        List<Contact> contacts = contactRepository.findByEmail("same@example.com");

        // Then
        assertThat(contacts).hasSize(2);
        assertThat(contacts).allMatch(c -> c.getEmail().equals("same@example.com"));
    }
}
