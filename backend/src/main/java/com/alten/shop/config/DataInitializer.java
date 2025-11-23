package com.alten.shop.config;

import com.alten.shop.model.InventoryStatus;
import com.alten.shop.model.Product;
import com.alten.shop.model.User;
import com.alten.shop.repository.ProductRepository;
import com.alten.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create admin user
        if (!userRepository.existsByEmail("admin@admin.com")) {
            User admin = User.builder()
                    .username("admin")
                    .firstname("Admin")
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("admin123"))
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created: admin@admin.com / admin123");
        }

        System.out.println("Initializing database with sample products...");

        List<Product> products = Arrays.asList(
                Product.builder()
                        .code("LAPTOP001")
                        .name("Dell XPS 15")
                        .description("High-performance laptop with 15.6-inch display, Intel i7, 16GB RAM")
                        .image("https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400")
                        .category("Electronics")
                        .price(1299.99)
                        .quantity(15)
                        .internalReference("DELL-XPS-15-2024")
                        .shellId(1L)
                        .inventoryStatus(InventoryStatus.INSTOCK)
                        .rating(4.7)
                        .build(),

                Product.builder()
                        .code("PHONE001")
                        .name("iPhone 15 Pro")
                        .description("Latest iPhone with A17 Pro chip, titanium design, 256GB storage")
                        .image("https://images.unsplash.com/photo-1592286927505-38c8f877d0f0?w=400")
                        .category("Electronics")
                        .price(999.99)
                        .quantity(25)
                        .internalReference("APPLE-IP15P-256")
                        .shellId(2L)
                        .inventoryStatus(InventoryStatus.INSTOCK)
                        .rating(4.9)
                        .build(),

                Product.builder()
                        .code("HEADPHONE001")
                        .name("Sony WH-1000XM5")
                        .description("Premium noise-canceling wireless headphones")
                        .image("https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=400")
                        .category("Electronics")
                        .price(399.99)
                        .quantity(8)
                        .internalReference("SONY-WH1000XM5")
                        .shellId(3L)
                        .inventoryStatus(InventoryStatus.LOWSTOCK)
                        .rating(4.8)
                        .build(),

                Product.builder()
                        .code("TABLET001")
                        .name("iPad Pro 12.9\"")
                        .description("Powerful tablet with M2 chip, 12.9-inch Liquid Retina display")
                        .image("https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400")
                        .category("Electronics")
                        .price(1099.99)
                        .quantity(12)
                        .internalReference("APPLE-IPADPRO-129")
                        .shellId(4L)
                        .inventoryStatus(InventoryStatus.INSTOCK)
                        .rating(4.6)
                        .build(),

                Product.builder()
                        .code("WATCH001")
                        .name("Apple Watch Series 9")
                        .description("Advanced health and fitness smartwatch")
                        .image("https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=400")
                        .category("Electronics")
                        .price(429.99)
                        .quantity(20)
                        .internalReference("APPLE-WATCH-S9")
                        .shellId(5L)
                        .inventoryStatus(InventoryStatus.INSTOCK)
                        .rating(4.5)
                        .build(),

                Product.builder()
                        .code("CAMERA001")
                        .name("Canon EOS R6")
                        .description("Professional mirrorless camera with 20.1MP sensor")
                        .image("https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=400")
                        .category("Electronics")
                        .price(2499.99)
                        .quantity(5)
                        .internalReference("CANON-EOSR6")
                        .shellId(6L)
                        .inventoryStatus(InventoryStatus.LOWSTOCK)
                        .rating(4.9)
                        .build(),

                Product.builder()
                        .code("KEYBOARD001")
                        .name("Logitech MX Keys")
                        .description("Advanced wireless illuminated keyboard")
                        .image("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400")
                        .category("Accessories")
                        .price(99.99)
                        .quantity(30)
                        .internalReference("LOGI-MXKEYS")
                        .shellId(7L)
                        .inventoryStatus(InventoryStatus.INSTOCK)
                        .rating(4.4)
                        .build(),

                Product.builder()
                        .code("MOUSE001")
                        .name("Logitech MX Master 3S")
                        .description("Advanced wireless mouse with precision scrolling")
                        .image("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400")
                        .category("Accessories")
                        .price(99.99)
                        .quantity(35)
                        .internalReference("LOGI-MXMASTER3S")
                        .shellId(8L)
                        .inventoryStatus(InventoryStatus.INSTOCK)
                        .rating(4.7)
                        .build(),

                Product.builder()
                        .code("MONITOR001")
                        .name("LG UltraWide 34\"")
                        .description("34-inch curved ultrawide monitor, 3440x1440 resolution")
                        .image("https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400")
                        .category("Electronics")
                        .price(699.99)
                        .quantity(10)
                        .internalReference("LG-UW34")
                        .shellId(9L)
                        .inventoryStatus(InventoryStatus.INSTOCK)
                        .rating(4.6)
                        .build(),

                Product.builder()
                        .code("SPEAKER001")
                        .name("Bose SoundLink Revolve+")
                        .description("Portable Bluetooth speaker with 360-degree sound")
                        .image("https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400")
                        .category("Electronics")
                        .price(299.99)
                        .quantity(0)
                        .internalReference("BOSE-SLRP")
                        .shellId(10L)
                        .inventoryStatus(InventoryStatus.OUTOFSTOCK)
                        .rating(4.5)
                        .build(),

                Product.builder()
                        .code("CHARGER001")
                        .name("Anker PowerPort III")
                        .description("65W USB-C fast charger with GaN technology")
                        .image("https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=400")
                        .category("Accessories")
                        .price(49.99)
                        .quantity(50)
                        .internalReference("ANKER-PP3-65W")
                        .shellId(11L)
                        .inventoryStatus(InventoryStatus.INSTOCK)
                        .rating(4.3)
                        .build(),

                Product.builder()
                        .code("CABLE001")
                        .name("USB-C to USB-C Cable")
                        .description("Braided 2m USB-C cable, 100W power delivery")
                        .image("https://images.unsplash.com/photo-1591290619762-d71b2e2a4c14?w=400")
                        .category("Accessories")
                        .price(19.99)
                        .quantity(100)
                        .internalReference("CABLE-USBC-2M")
                        .shellId(12L)
                        .inventoryStatus(InventoryStatus.INSTOCK)
                        .rating(4.2)
                        .build());

        productRepository.saveAll(products);
        System.out.println("Database initialized with " + products.size() + " products");
    }
}
