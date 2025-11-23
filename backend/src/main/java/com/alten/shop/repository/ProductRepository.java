package com.alten.shop.repository;

import com.alten.shop.model.InventoryStatus;
import com.alten.shop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    List<Product> findByCategory(String category);

    List<Product> findByInventoryStatus(InventoryStatus inventoryStatus);

    Page<Product> findAll(Pageable pageable);
}
