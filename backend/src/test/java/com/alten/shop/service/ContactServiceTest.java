package com.alten.shop.service;

import com.alten.shop.model.Contact;
import com.alten.shop.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    private Contact testContact;

    @BeforeEach
    void setUp() {
        testContact = Contact.builder()
                .id(1L)
                .email("test@example.com")
                .message("Test message")
                .build();
    }

    @Test
    void whenSubmitContact_thenReturnSavedContact() {
        // Given
        Contact newContact = Contact.builder()
                .email("new@example.com")
                .message("New message")
                .build();
        when(contactRepository.save(any(Contact.class))).thenReturn(newContact);

        // When
        Contact result = contactService.submitContact(newContact);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getMessage()).isEqualTo("New message");
        verify(contactRepository, times(1)).save(newContact);
    }

    @Test
    void whenGetAllContacts_thenReturnContactList() {
        // Given
        List<Contact> contacts = Arrays.asList(testContact);
        when(contactRepository.findAll()).thenReturn(contacts);

        // When
        List<Contact> result = contactService.getAllContacts();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void whenGetContactsByEmail_thenReturnFilteredContacts() {
        // Given
        List<Contact> contacts = Arrays.asList(testContact);
        when(contactRepository.findByEmail("test@example.com")).thenReturn(contacts);

        // When
        List<Contact> result = contactService.getContactsByEmail("test@example.com");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
        verify(contactRepository, times(1)).findByEmail("test@example.com");
    }
}
