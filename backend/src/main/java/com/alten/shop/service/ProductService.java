package com.alten.shop.service;

import com.alten.shop.model.InventoryStatus;
import com.alten.shop.model.Product;
import com.alten.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product getProductByCode(String code) {
        return productRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Product not found with code: " + code));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);

        product.setCode(productDetails.getCode());
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setImage(productDetails.getImage());
        product.setCategory(productDetails.getCategory());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());
        product.setInternalReference(productDetails.getInternalReference());
        product.setShellId(productDetails.getShellId());
        product.setInventoryStatus(productDetails.getInventoryStatus());
        product.setRating(productDetails.getRating());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        getProductById(id); // Verify product exists
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsByInventoryStatus(InventoryStatus status) {
        return productRepository.findByInventoryStatus(status);
    }
}
