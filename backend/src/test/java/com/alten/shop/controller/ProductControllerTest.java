package com.alten.shop.controller;

import com.alten.shop.model.InventoryStatus;
import com.alten.shop.model.Product;
import com.alten.shop.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .code("PROD001")
                .name("Test Product")
                .description("Test Description")
                .image("https://example.com/image.jpg")
                .category("Electronics")
                .price(99.99)
                .quantity(10)
                .internalReference("REF001")
                .shellId(1L)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(4.5)
                .build();
    }

    @Test
    void whenGetAllProducts_thenReturnProductList() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code").value("PROD001"))
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].price").value(99.99));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void whenGetAllProductsWithPagination_thenReturnPagedProducts() throws Exception {
        // Given
        Page<Product> page = new PageImpl<>(Arrays.asList(testProduct));
        when(productService.getAllProducts(any(PageRequest.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].code").value("PROD001"));

        verify(productService, times(1)).getAllProducts(any(PageRequest.class));
    }

    @Test
    void whenGetProductById_thenReturnProduct() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PROD001"))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void whenGetProductByIdNotFound_thenReturn404() throws Exception {
        // Given
        when(productService.getProductById(999L))
                .thenThrow(new RuntimeException("Product not found"));

        // When & Then
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    void whenCreateProduct_thenReturnCreatedProduct() throws Exception {
        // Given
        Product newProduct = Product.builder()
                .code("PROD002")
                .name("New Product")
                .price(149.99)
                .quantity(5)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(4.0)
                .build();
        when(productService.createProduct(any(Product.class))).thenReturn(newProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("PROD002"))
                .andExpect(jsonPath("$.name").value("New Product"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void whenCreateProductWithInvalidData_thenReturn400() throws Exception {
        // Given - product with negative price
        Product invalidProduct = Product.builder()
                .code("PROD003")
                .name("Invalid Product")
                .price(-10.0)
                .quantity(5)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(4.0)
                .build();

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateProduct_thenReturnUpdatedProduct() throws Exception {
        // Given
        Product updatedProduct = Product.builder()
                .id(1L)
                .code("PROD001")
                .name("Updated Product")
                .price(199.99)
                .quantity(20)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(5.0)
                .build();
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(199.99));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void whenDeleteProduct_thenReturn204() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void whenGetProductsByCategory_thenReturnFilteredProducts() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProductsByCategory("Electronics")).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category").value("Electronics"));

        verify(productService, times(1)).getProductsByCategory("Electronics");
    }

    @Test
    void whenGetProductsByInventoryStatus_thenReturnFilteredProducts() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProductsByInventoryStatus(InventoryStatus.INSTOCK)).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products/status/INSTOCK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].inventoryStatus").value("INSTOCK"));

        verify(productService, times(1)).getProductsByInventoryStatus(InventoryStatus.INSTOCK);
    }
}
