package com.alten.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenRegister_thenReturnAuthResponse() throws Exception {
        String registerPayload = """
                {
                    "username": "testuser",
                    "firstname": "Test",
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void whenRegisterWithBlankUsername_thenReturn400() throws Exception {
        String registerPayload = """
                {
                    "username": "",
                    "firstname": "Test",
                    "email": "test2@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenRegisterWithInvalidEmail_thenReturn400() throws Exception {
        String registerPayload = """
                {
                    "username": "testuser",
                    "firstname": "Test",
                    "email": "invalid-email",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenLogin_thenReturnAuthResponse() throws Exception {
        // First register a user
        String registerPayload = """
                {
                    "username": "logintest",
                    "firstname": "Login",
                    "email": "login@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload))
                .andExpect(status().isCreated());

        // Then login
        String loginPayload = """
                {
                    "email": "login@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("login@example.com"));
    }

    @Test
    void whenLoginWithInvalidEmail_thenReturn400() throws Exception {
        String loginPayload = """
                {
                    "email": "invalid-email",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenLoginWithBlankPassword_thenReturn400() throws Exception {
        String loginPayload = """
                {
                    "email": "test@example.com",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
                .andExpect(status().isBadRequest());
    }
}
