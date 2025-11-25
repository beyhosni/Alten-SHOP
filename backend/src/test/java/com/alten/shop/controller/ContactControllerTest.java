package com.alten.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void whenSubmitContact_thenReturnCreatedContact() throws Exception {
        String contactPayload = """
                {
                    "email": "contact@example.com",
                    "message": "This is a test contact message"
                }
                """;

        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contactPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("contact@example.com"))
                .andExpect(jsonPath("$.message").value("This is a test contact message"));
    }

    @Test
    @WithMockUser
    void whenSubmitContactWithInvalidEmail_thenReturn400() throws Exception {
        String contactPayload = """
                {
                    "email": "invalid-email",
                    "message": "This is a test message"
                }
                """;

        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contactPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenSubmitContactWithBlankMessage_thenReturn400() throws Exception {
        String contactPayload = """
                {
                    "email": "test@example.com",
                    "message": ""
                }
                """;

        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contactPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenSubmitContactWithMessageTooLong_thenReturn400() throws Exception {
        String longMessage = "a".repeat(301);
        String contactPayload = String.format("""
                {
                    "email": "test@example.com",
                    "message": "%s"
                }
                """, longMessage);

        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contactPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenGetAllContacts_thenReturnContactList() throws Exception {
        mockMvc.perform(get("/api/contact"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
