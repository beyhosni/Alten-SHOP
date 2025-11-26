package com.alten.shop;

import com.alten.shop.dto.LoginRequest;
import com.alten.shop.dto.RegisterRequest;
import com.alten.shop.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("E2E Application Integration Tests")
@Transactional
class ShopApplicationE2ETest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ProductRepository productRepository;

        private String authToken;

        @BeforeEach
        void setUp() throws Exception {
                // Clear repositories
                userRepository.deleteAll();
                productRepository.deleteAll();

                // Register user
                RegisterRequest registerRequest = new RegisterRequest(
                                "testuser",
                                "Test",
                                "e2e@test.com",
                                "password123"
                );

                mockMvc.perform(post("/account")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated());

                // Login
                LoginRequest loginRequest = new LoginRequest(
                                "e2e@test.com",
                                "password123"
                );

                MvcResult result = mockMvc.perform(post("/token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                String response = result.getResponse().getContentAsString();
                authToken = objectMapper.readTree(response).get("token").asText();
        }

        @Nested
        @DisplayName("Product Management E2E Tests")
        class ProductManagementTests {
                @Test
                @DisplayName("Should retrieve products list")
                void shouldRetrieveProductsList() throws Exception {
                        mockMvc.perform(get("/api/products")
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("Should retrieve products by category")
                void shouldRetrieveProductsByCategory() throws Exception {
                        mockMvc.perform(get("/api/products/category/Electronics")
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isOk());
                }
        }

        @Nested
        @DisplayName("Authentication E2E Tests")
        class AuthenticationTests {
                @Test
                @DisplayName("Register new user successfully")
                void registerNewUserSuccessfully() throws Exception {
                        RegisterRequest registerRequest = new RegisterRequest(
                                        "newuser",
                                        "New",
                                        "newuser@test.com",
                                        "password456"
                        );

                        mockMvc.perform(post("/account")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(registerRequest)))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.token").isNotEmpty());
                }

                @Test
                @DisplayName("Register with duplicate email should fail")
                void registerWithDuplicateEmailShouldFail() throws Exception {
                        RegisterRequest registerRequest = new RegisterRequest(
                                        "duplicateuser",
                                        "Duplicate",
                                        "e2e@test.com",
                                        "password123"
                        );

                        mockMvc.perform(post("/account")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(registerRequest)))
                                        .andExpect(status().isConflict());
                }

                @Test
                @DisplayName("Login with correct credentials")
                void loginWithCorrectCredentials() throws Exception {
                        LoginRequest loginRequest = new LoginRequest(
                                        "e2e@test.com",
                                        "password123"
                        );

                        mockMvc.perform(post("/token")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(loginRequest)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.token").isNotEmpty());
                }

                @Test
                @DisplayName("Login with wrong password should fail")
                void loginWithWrongPasswordShouldFail() throws Exception {
                        LoginRequest loginRequest = new LoginRequest(
                                        "e2e@test.com",
                                        "wrongpassword"
                        );

                        mockMvc.perform(post("/token")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(loginRequest)))
                                        .andExpect(status().isForbidden());
                }
        }

        @Nested
        @DisplayName("Authorization E2E Tests")
        class AuthorizationTests {
                @Test
                @DisplayName("Access protected endpoint without token should fail")
                void accessProtectedEndpointWithoutTokenShouldFail() throws Exception {
                        mockMvc.perform(get("/api/cart"))
                                        .andExpect(status().isUnauthorized());
                }

                @Test
                @DisplayName("Access protected endpoint with invalid token should fail")
                void accessProtectedEndpointWithInvalidTokenShouldFail() throws Exception {
                        mockMvc.perform(get("/api/cart")
                                        .header("Authorization", "Bearer invalid-token"))
                                        .andExpect(status().isUnauthorized());
                }

                @Test
                @DisplayName("Access protected endpoint with valid token should succeed")
                void accessProtectedEndpointWithValidTokenShouldSucceed() throws Exception {
                        mockMvc.perform(get("/api/cart")
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isOk());
                }
        }

        @Nested
        @DisplayName("Cart Management E2E Tests")
        class CartManagementTests {
                @Test
                @DisplayName("Should add product to cart")
                void shouldAddProductToCart() throws Exception {
                        // First, create a product
                        String productJson = '{"name":"Test Product","code":"TP001","price":100.0,"quantity":10,"category":"Electronics","inventoryStatus":"INSTOCK"}";

                        MvcResult result = mockMvc.perform(post("/api/products")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(productJson))
                                        .andExpect(status().isCreated())
                                        .andReturn();

                        String response = result.getResponse().getContentAsString();
                        Long productId = objectMapper.readTree(response).get("id").asLong();

                        // Add product to cart
                        String addToCartJson = '{"productId":' + productId + ',"quantity":2}';

                        mockMvc.perform(post("/api/cart/items")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(addToCartJson))
                                        .andExpect(status().isOk());

                        // Verify product quantity was reduced
                        mockMvc.perform(get("/api/products/" + productId)
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.quantity").value(8)); // 10 - 2
                }

                @Test
                @DisplayName("Should checkout with non-empty cart")
                void shouldCheckoutWithNonEmptyCart() throws Exception {
                        // First, create a product
                        String productJson = '{"name":"Test Product","code":"TP002","price":100.0,"quantity":10,"category":"Electronics","inventoryStatus":"INSTOCK"}";

                        MvcResult result = mockMvc.perform(post("/api/products")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(productJson))
                                        .andExpect(status().isCreated())
                                        .andReturn();

                        String response = result.getResponse().getContentAsString();
                        Long productId = objectMapper.readTree(response).get("id").asLong();

                        // Add product to cart
                        String addToCartJson = '{"productId":' + productId + ',"quantity":2}';

                        mockMvc.perform(post("/api/cart/items")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(addToCartJson))
                                        .andExpect(status().isOk());

                        // Checkout
                        mockMvc.perform(post("/api/cart/checkout")
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isOk())
                                        .andExpect(content().string("Order placed successfully"));

                        // Verify cart is empty
                        mockMvc.perform(get("/api/cart")
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.items").isEmpty());
                }

                @Test
                @DisplayName("Should not checkout with empty cart")
                void shouldNotCheckoutWithEmptyCart() throws Exception {
                        mockMvc.perform(post("/api/cart/checkout")
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isBadRequest());
                }
        }
}
