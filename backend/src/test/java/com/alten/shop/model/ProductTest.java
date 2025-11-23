package com.alten.shop.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoConstraintViolations() {
        // Given
        Product product = Product.builder()
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

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void whenCodeIsNull_thenConstraintViolation() {
        // Given
        Product product = Product.builder()
                .name("Test Product")
                .price(99.99)
                .quantity(10)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(4.5)
                .build();

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("code"));
    }

    @Test
    void whenNameIsBlank_thenConstraintViolation() {
        // Given
        Product product = Product.builder()
                .code("PROD001")
                .name("")
                .price(99.99)
                .quantity(10)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(4.5)
                .build();

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void whenPriceIsNegative_thenConstraintViolation() {
        // Given
        Product product = Product.builder()
                .code("PROD001")
                .name("Test Product")
                .price(-10.0)
                .quantity(10)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(4.5)
                .build();

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    @Test
    void whenQuantityIsNegative_thenConstraintViolation() {
        // Given
        Product product = Product.builder()
                .code("PROD001")
                .name("Test Product")
                .price(99.99)
                .quantity(-5)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(4.5)
                .build();

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("quantity"));
    }

    @Test
    void whenRatingIsOutOfRange_thenConstraintViolation() {
        // Given - rating > 5
        Product product = Product.builder()
                .code("PROD001")
                .name("Test Product")
                .price(99.99)
                .quantity(10)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(6.0)
                .build();

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("rating"));
    }

    @Test
    void whenInventoryStatusIsNull_thenConstraintViolation() {
        // Given
        Product product = Product.builder()
                .code("PROD001")
                .name("Test Product")
                .price(99.99)
                .quantity(10)
                .rating(4.5)
                .build();

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("inventoryStatus"));
    }

    @Test
    void whenCreatingProduct_thenTimestampsAreSet() {
        // Given & When
        Product product = new Product();
        product.setCode("PROD001");
        product.setName("Test Product");
        product.setPrice(99.99);
        product.setQuantity(10);
        product.setInventoryStatus(InventoryStatus.INSTOCK);
        product.setRating(4.5);

        // Then
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    @Test
    void whenUpdatingProduct_thenUpdatedAtChanges() throws InterruptedException {
        // Given
        Product product = new Product();
        product.setCode("PROD001");
        product.setName("Test Product");
        product.setPrice(99.99);
        product.setQuantity(10);
        product.setInventoryStatus(InventoryStatus.INSTOCK);
        product.setRating(4.5);

        Long originalUpdatedAt = product.getUpdatedAt();

        // Wait a bit to ensure timestamp difference
        Thread.sleep(10);

        // When
        product.setName("Updated Product");

        // Then
        assertThat(product.getUpdatedAt()).isGreaterThan(originalUpdatedAt);
    }
}
