package com.alten.shop.service;

import com.alten.shop.dto.AddToCartRequest;
import com.alten.shop.model.Cart;
import com.alten.shop.model.CartItem;
import com.alten.shop.model.Product;
import com.alten.shop.model.User;
import com.alten.shop.repository.CartItemRepository;
import com.alten.shop.repository.CartRepository;
import com.alten.shop.repository.ProductRepository;
import com.alten.shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(100.0)
                .quantity(10)
                .build();

        testCart = Cart.builder()
                .id(1L)
                .user(testUser)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void whenGetOrCreateCart_andCartExists_thenReturnCart() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));

        Cart result = cartService.getOrCreateCart(testUser.getEmail());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testCart.getId());
    }

    @Test
    void whenGetOrCreateCart_andCartDoesNotExist_thenCreateCart() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.getOrCreateCart(testUser.getEmail());

        assertThat(result).isNotNull();
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void whenAddToCart_andItemIsNew_thenAddItem() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartIdAndProductId(testCart.getId(), testProduct.getId()))
                .thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.addToCart(testUser.getEmail(), request);

        assertThat(result).isNotNull();
        verify(cartItemRepository).save(any(CartItem.class));
        verify(productRepository).save(testProduct);
        assertThat(testProduct.getQuantity()).isEqualTo(8); // 10 - 2
    }

    @Test
    void whenAddToCart_andItemExists_thenUpdateQuantity() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        CartItem existingItem = CartItem.builder()
                .id(1L)
                .cart(testCart)
                .product(testProduct)
                .quantity(1)
                .build();

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartIdAndProductId(testCart.getId(), testProduct.getId()))
                .thenReturn(Optional.of(existingItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.addToCart(testUser.getEmail(), request);

        assertThat(result).isNotNull();
        assertThat(existingItem.getQuantity()).isEqualTo(3); // 1 + 2
        verify(productRepository).save(testProduct);
    }

    @Test
    void whenRemoveFromCart_thenRemoveItemAndRestoreStock() {
        CartItem itemToRemove = CartItem.builder()
                .id(1L)
                .cart(testCart)
                .product(testProduct)
                .quantity(2)
                .build();
        testCart.getItems().add(itemToRemove);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(itemToRemove));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        cartService.removeFromCart(testUser.getEmail(), 1L);

        verify(cartItemRepository).delete(itemToRemove);
        verify(productRepository).save(testProduct);
        assertThat(testProduct.getQuantity()).isEqualTo(12); // 10 + 2
    }

    @Test
    void whenCheckout_withEmptyCart_thenThrowException() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));

        try {
            cartService.checkout(testUser.getEmail());
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Cannot checkout with an empty cart");
        }
    }

    @Test
    void whenCheckout_withNonEmptyCart_thenClearCart() {
        CartItem item = CartItem.builder()
                .id(1L)
                .cart(testCart)
                .product(testProduct)
                .quantity(2)
                .build();
        testCart.getItems().add(item);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        cartService.checkout(testUser.getEmail());

        verify(cartItemRepository).deleteByCartId(testCart.getId());
        verify(cartRepository).save(testCart);
        assertThat(testCart.getItems()).isEmpty();
    }
}
