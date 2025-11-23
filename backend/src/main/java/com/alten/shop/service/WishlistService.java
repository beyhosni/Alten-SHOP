package com.alten.shop.service;

import com.alten.shop.model.Product;
import com.alten.shop.model.User;
import com.alten.shop.model.Wishlist;
import com.alten.shop.model.WishlistItem;
import com.alten.shop.repository.ProductRepository;
import com.alten.shop.repository.UserRepository;
import com.alten.shop.repository.WishlistItemRepository;
import com.alten.shop.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Wishlist getOrCreateWishlist(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return wishlistRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Wishlist wishlist = Wishlist.builder()
                            .user(user)
                            .build();
                    return wishlistRepository.save(wishlist);
                });
    }

    @Transactional
    public Wishlist addToWishlist(String userEmail, Long productId) {
        Wishlist wishlist = getOrCreateWishlist(userEmail);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if item already exists
        var existingItem = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), product.getId());

        if (existingItem.isEmpty()) {
            WishlistItem newItem = WishlistItem.builder()
                    .wishlist(wishlist)
                    .product(product)
                    .build();
            wishlist.addItem(newItem);
            wishlistItemRepository.save(newItem);
        }

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public Wishlist removeFromWishlist(String userEmail, Long itemId) {
        Wishlist wishlist = getOrCreateWishlist(userEmail);
        WishlistItem item = wishlistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));

        if (!item.getWishlist().getId().equals(wishlist.getId())) {
            throw new RuntimeException("Wishlist item does not belong to user");
        }

        wishlist.removeItem(item);
        wishlistItemRepository.delete(item);
        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public void clearWishlist(String userEmail) {
        Wishlist wishlist = getOrCreateWishlist(userEmail);
        wishlistItemRepository.deleteByWishlistId(wishlist.getId());
        wishlist.getItems().clear();
        wishlistRepository.save(wishlist);
    }
}
