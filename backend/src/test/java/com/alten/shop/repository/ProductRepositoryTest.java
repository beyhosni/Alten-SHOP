package com.alten.shop.repository;

import com.alten.shop.model.InventoryStatus;
import com.alten.shop.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void whenSaveProduct_thenProductIsPersisted() {
        // Given
        Product product = createTestProduct("PROD001", "Test Product", 99.99);

        // When
        Product saved = productRepository.save(product);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCode()).isEqualTo("PROD001");
        assertThat(saved.getName()).isEqualTo("Test Product");
    }

    @Test
    void whenFindById_thenReturnProduct() {
        // Given
        Product product = createTestProduct("PROD002", "Test Product 2", 149.99);
        Product saved = entityManager.persistAndFlush(product);

        // When
        Optional<Product> found = productRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("PROD002");
    }

    @Test
    void whenFindByCode_thenReturnProduct() {
        // Given
        Product product = createTestProduct("PROD003", "Test Product 3", 199.99);
        entityManager.persistAndFlush(product);

        // When
        Optional<Product> found = productRepository.findByCode("PROD003");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Product 3");
    }

    @Test
    void whenFindAll_thenReturnAllProducts() {
        // Given
        Product product1 = createTestProduct("PROD004", "Product 1", 99.99);
        Product product2 = createTestProduct("PROD005", "Product 2", 149.99);
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush();

        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertThat(products).hasSize(2);
    }

    @Test
    void whenFindAllWithPagination_thenReturnPagedProducts() {
        // Given
        for (int i = 0; i < 15; i++) {
            Product product = createTestProduct("PROD" + i, "Product " + i, 99.99 + i);
            entityManager.persist(product);
        }
        entityManager.flush();

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = productRepository.findAll(pageable);

        // Then
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    void whenFindByCategory_thenReturnProductsInCategory() {
        // Given
        Product electronics1 = createTestProduct("ELEC001", "Laptop", 999.99);
        electronics1.setCategory("Electronics");
        Product electronics2 = createTestProduct("ELEC002", "Phone", 599.99);
        electronics2.setCategory("Electronics");
        Product clothing = createTestProduct("CLOTH001", "Shirt", 29.99);
        clothing.setCategory("Clothing");

        entityManager.persist(electronics1);
        entityManager.persist(electronics2);
        entityManager.persist(clothing);
        entityManager.flush();

        // When
        List<Product> electronicsProducts = productRepository.findByCategory("Electronics");

        // Then
        assertThat(electronicsProducts).hasSize(2);
        assertThat(electronicsProducts).allMatch(p -> p.getCategory().equals("Electronics"));
    }

    @Test
    void whenFindByInventoryStatus_thenReturnProductsWithStatus() {
        // Given
        Product inStock1 = createTestProduct("IN001", "In Stock 1", 99.99);
        inStock1.setInventoryStatus(InventoryStatus.INSTOCK);
        Product inStock2 = createTestProduct("IN002", "In Stock 2", 149.99);
        inStock2.setInventoryStatus(InventoryStatus.INSTOCK);
        Product outOfStock = createTestProduct("OUT001", "Out of Stock", 199.99);
        outOfStock.setInventoryStatus(InventoryStatus.OUTOFSTOCK);

        entityManager.persist(inStock1);
        entityManager.persist(inStock2);
        entityManager.persist(outOfStock);
        entityManager.flush();

        // When
        List<Product> inStockProducts = productRepository.findByInventoryStatus(InventoryStatus.INSTOCK);

        // Then
        assertThat(inStockProducts).hasSize(2);
        assertThat(inStockProducts).allMatch(p -> p.getInventoryStatus() == InventoryStatus.INSTOCK);
    }

    @Test
    void whenDeleteProduct_thenProductIsRemoved() {
        // Given
        Product product = createTestProduct("DEL001", "To Delete", 99.99);
        Product saved = entityManager.persistAndFlush(product);

        // When
        productRepository.deleteById(saved.getId());
        entityManager.flush();

        // Then
        Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void whenUpdateProduct_thenProductIsUpdated() {
        // Given
        Product product = createTestProduct("UPD001", "Original Name", 99.99);
        Product saved = entityManager.persistAndFlush(product);

        // When
        saved.setName("Updated Name");
        saved.setPrice(149.99);
        Product updated = productRepository.save(saved);
        entityManager.flush();

        // Then
        Product found = productRepository.findById(updated.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Updated Name");
        assertThat(found.getPrice()).isEqualTo(149.99);
    }

    private Product createTestProduct(String code, String name, Double price) {
        return Product.builder()
                .code(code)
                .name(name)
                .description("Test description")
                .image("https://example.com/image.jpg")
                .category("Test Category")
                .price(price)
                .quantity(10)
                .internalReference("REF-" + code)
                .shellId(1L)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(4.5)
                .build();
    }
}
