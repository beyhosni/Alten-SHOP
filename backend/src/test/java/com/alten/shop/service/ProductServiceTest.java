package com.alten.shop.service;

import com.alten.shop.model.InventoryStatus;
import com.alten.shop.model.Product;
import com.alten.shop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .code("PROD001")
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .quantity(10)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(4.5)
                .build();
    }

    @Test
    void whenGetAllProducts_thenReturnProductList() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        // When
        List<Product> result = productService.getAllProducts();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("PROD001");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllProductsWithPagination_thenReturnPagedProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(Arrays.asList(testProduct));
        when(productRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<Product> result = productService.getAllProducts(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCode()).isEqualTo("PROD001");
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void whenGetProductById_thenReturnProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Product result = productService.getProductById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("PROD001");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetProductByIdNotFound_thenThrowException() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void whenGetProductByCode_thenReturnProduct() {
        // Given
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(testProduct));

        // When
        Product result = productService.getProductByCode("PROD001");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findByCode("PROD001");
    }

    @Test
    void whenCreateProduct_thenReturnSavedProduct() {
        // Given
        Product newProduct = Product.builder()
                .code("PROD002")
                .name("New Product")
                .price(149.99)
                .quantity(5)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(4.0)
                .build();
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        // When
        Product result = productService.createProduct(newProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("PROD002");
        verify(productRepository, times(1)).save(newProduct);
    }

    @Test
    void whenUpdateProduct_thenReturnUpdatedProduct() {
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
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // When
        Product result = productService.updateProduct(1L, updatedProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Product");
        assertThat(result.getPrice()).isEqualTo(199.99);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void whenDeleteProduct_thenProductIsDeleted() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).deleteById(1L);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenGetProductsByCategory_thenReturnFilteredProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategory("Electronics")).thenReturn(products);

        // When
        List<Product> result = productService.getProductsByCategory("Electronics");

        // Then
        assertThat(result).hasSize(1);
        verify(productRepository, times(1)).findByCategory("Electronics");
    }

    @Test
    void whenGetProductsByInventoryStatus_thenReturnFilteredProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByInventoryStatus(InventoryStatus.INSTOCK)).thenReturn(products);

        // When
        List<Product> result = productService.getProductsByInventoryStatus(InventoryStatus.INSTOCK);

        // Then
        assertThat(result).hasSize(1);
        verify(productRepository, times(1)).findByInventoryStatus(InventoryStatus.INSTOCK);
    }
}
