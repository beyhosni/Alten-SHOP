
package com.alten.shop.controller;

import com.alten.shop.dto.AddToCartRequest;
import com.alten.shop.model.Cart;
import com.alten.shop.model.CartItem;
import com.alten.shop.model.Product;
import com.alten.shop.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cart testCart;
    private AddToCartRequest addToCartRequest;

    @BeforeEach
    void setUp() {
        testCart = Cart.builder()
                .id(1L)
                .items(new ArrayList<>())
                .build();

        addToCartRequest = new AddToCartRequest();
        addToCartRequest.setProductId(1L);
        addToCartRequest.setQuantity(2);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void whenGetCart_thenReturnCart() throws Exception {
        when(cartService.getOrCreateCart("test@example.com")).thenReturn(testCart);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testCart.getId()));

        verify(cartService).getOrCreateCart("test@example.com");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void whenAddToCart_thenReturnUpdatedCart() throws Exception {
        when(cartService.addToCart(any(AddToCartRequest.class))).thenReturn(testCart);

        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testCart.getId()));

        verify(cartService).addToCart(any(AddToCartRequest.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void whenUpdateCartItemQuantity_thenReturnUpdatedCart() throws Exception {
        when(cartService.updateQuantity(1L, 3)).thenReturn(testCart);

        mockMvc.perform(put("/api/cart/items/1")
                .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testCart.getId()));

        verify(cartService).updateQuantity(1L, 3);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void whenRemoveFromCart_thenReturnUpdatedCart() throws Exception {
        when(cartService.removeItem(1L)).thenReturn(testCart);

        mockMvc.perform(delete("/api/cart/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testCart.getId()));

        verify(cartService).removeItem(1L);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void whenClearCart_thenNoContent() throws Exception {
        doNothing().when(cartService).clearCart();

        mockMvc.perform(delete("/api/cart"))
                .andExpect(status().isNoContent());

        verify(cartService).clearCart();
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void whenCheckout_thenReturnSuccessMessage() throws Exception {
        doNothing().when(cartService).checkout();

        mockMvc.perform(post("/api/cart/checkout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order placed successfully"));

        verify(cartService).checkout();
    }
}
