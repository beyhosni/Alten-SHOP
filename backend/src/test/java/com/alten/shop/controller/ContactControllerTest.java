package com.alten.shop.controller;

import com.alten.shop.model.Contact;
import com.alten.shop.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
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
    void whenSubmitContact_thenReturnCreatedContact() throws Exception {
        // Given
        Contact newContact = Contact.builder()
                .email("new@example.com")
                .message("New message")
                .build();
        when(contactService.submitContact(any(Contact.class))).thenReturn(newContact);

        // When & Then
        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newContact)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.message").value("New message"));

        verify(contactService, times(1)).submitContact(any(Contact.class));
    }

    @Test
    void whenSubmitContactWithInvalidEmail_thenReturn400() throws Exception {
        // Given - contact with invalid email
        Contact invalidContact = Contact.builder()
                .email("invalid-email")
                .message("Test message")
                .build();

        // When & Then
        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidContact)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenSubmitContactWithBlankMessage_thenReturn400() throws Exception {
        // Given - contact with blank message
        Contact invalidContact = Contact.builder()
                .email("test@example.com")
                .message("")
                .build();

        // When & Then
        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidContact)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenSubmitContactWithMessageTooLong_thenReturn400() throws Exception {
        // Given - contact with message > 300 characters
        String longMessage = "a".repeat(301);
        Contact invalidContact = Contact.builder()
                .email("test@example.com")
                .message(longMessage)
                .build();

        // When & Then
        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidContact)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetAllContacts_thenReturnContactList() throws Exception {
        // Given
        List<Contact> contacts = Arrays.asList(testContact);
        when(contactService.getAllContacts()).thenReturn(contacts);

        // When & Then
        mockMvc.perform(get("/api/contact"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));

        verify(contactService, times(1)).getAllContacts();
    }
}
