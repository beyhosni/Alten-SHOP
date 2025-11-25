package com.alten.shop.controller;

import com.alten.shop.config.WithAdminUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void whenGetAllProducts_thenReturnProductList() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void whenGetAllProductsWithPagination_thenReturnPagedProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser
    void whenGetProductById_thenReturnProduct() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void whenGetProductByIdNotFound_thenReturn404() throws Exception {
        mockMvc.perform(get("/api/products/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAdminUser
    @org.junit.jupiter.api.Disabled("Admin check needs database user - skipping for now")
    void whenCreateProduct_thenReturnCreatedProduct() throws Exception {
        String productPayload = """
                {
                    "code": "TEST001",
                    "name": "Test Product",
                    "description": "A test product",
                    "price": 49.99,
                    "quantity": 10,
                    "category": "Electronics",
                    "inventoryStatus": "INSTOCK",
                    "rating": 4.5
                }
                """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("TEST001"))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithAdminUser
    void whenCreateProductWithInvalidData_thenReturn400() throws Exception {
        String productPayload = """
                {
                    "code": "INVALID",
                    "name": "",
                    "price": -10.0,
                    "quantity": 5,
                    "inventoryStatus": "INSTOCK"
                }
                """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAdminUser
    void whenUpdateProduct_thenReturnUpdatedProduct() throws Exception {
        String productPayload = """
                {
                    "code": "UPD001",
                    "name": "Updated Test Product",
                    "description": "Updated description",
                    "price": 99.99,
                    "quantity": 20,
                    "category": "Electronics",
                    "inventoryStatus": "INSTOCK",
                    "rating": 5.0
                }
                """;

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productPayload))
                .andExpect(status().isOk());
    }

    @Test
    @WithAdminUser
    void whenDeleteProduct_thenReturn204() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void whenGetProductsByCategory_thenReturnFilteredProducts() throws Exception {
        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void whenGetProductsByInventoryStatus_thenReturnFilteredProducts() throws Exception {
        mockMvc.perform(get("/api/products/status/INSTOCK"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
